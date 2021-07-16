package com.xixi.proxys;

import com.xixi.ApiServer;
import com.xixi.dto.MethodInfo;
import com.xixi.dto.ServerInfo;
import com.xixi.interfaces.ProxyCreator;
import com.xixi.interfaces.RestHandler;
import com.xixi.resthandlers.WebClientRestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * jdk动态代理
 *
 * @author markingWang
 * @date 2021/7/16 5:35 下午
 */
@Slf4j
public class JdkProxyCreator implements ProxyCreator {
    @Override
    public Object createProxy(Class<?> type) {
        log.info("createProxy: " + type);

        // 根据接口获取服务器信息
        ServerInfo serverInfo = extractServerInfo(type);
        log.info("serverInfo: " + serverInfo);


        // 给每一个代理类一个实现
        RestHandler restHandler = new WebClientRestHandler();
        // 初始化服务器信息
        restHandler.init(serverInfo);

        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 根据方法和参数得到调用信息
                MethodInfo methodInfo = extractMethodInfo(method, args);
                log.info("methodInfo:" + methodInfo);
                return restHandler.invokeRest(methodInfo);
            }

            /**
             * 获取调用信息
             * @param method 方法信息
             * @param args 参数信息
             * @return 调用信息
             */
            private MethodInfo extractMethodInfo(Method method, Object[] args) {
                MethodInfo methodInfo = new MethodInfo();
                // 设置请求URL和请求方法
                extractUrlAndMethod(method, methodInfo);
                // 设置请求参数和请求体
                extractRequestParamAndBody(method, args, methodInfo);
                // 设置返回对象的类型
                extractReturnInfo(method, methodInfo);
                return methodInfo;
            }

            /**
             * 设置返回对象的类型
             * @param method 方法
             * @param methodInfo 调用方法
             */
            private void extractReturnInfo(Method method, MethodInfo methodInfo) {
                // 设置返回flux还是mono
                // isAssignableFrom 判断类型是否某个的子类
                // instanceof 判断实例是否某个的子类
                boolean isFlux = method.getReturnType().isAssignableFrom(Flux.class);
                methodInfo.setReturnFlux(isFlux);

                // 得到返回对象的实际类型
                methodInfo.setReturnElementType(extractElementType(method.getGenericReturnType()));
            }

            /**
             * 设置调用方法的请求参数和请求体
             * @param method 方法
             * @param args 参数
             * @param methodInfo 调用方法
             */
            private void extractRequestParamAndBody(Method method, Object[] args, MethodInfo methodInfo) {
                // 设置请求参数和请求体
                Parameter[] parameters = method.getParameters();

                Map<String, Object> params = new LinkedHashMap<>();
                methodInfo.setParams(params);

                for (int i = 0; i < parameters.length; i++) {
                    // 是否带PathVariable注解
                    PathVariable annotationPath = parameters[i].getAnnotation(PathVariable.class);
                    if (annotationPath != null) {
                        params.put(annotationPath.value(), args[i]);
                    }

                    RequestBody annotationBody = parameters[i].getAnnotation(RequestBody.class);
                    if (annotationBody != null) {
                        methodInfo.setBody((Mono<?>) args[i]);
                        // 设置body的类型
                        methodInfo.setBodyElementType(extractElementType(parameters[i].getParameterizedType()));
                    }
                }
            }

            /**
             * 得到泛型类型的实际类型
             * @param genericType 生成的类型
             * @return 实际类型
             */
            private Class<?> extractElementType(Type genericType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericType)
                        .getActualTypeArguments();
                return (Class<?>) actualTypeArguments[0];
            }

            /**
             * 设置调用的url和方法
             * @param method  方法
             * @param methodInfo 调用信息方法
             */
            private void extractUrlAndMethod(Method method, MethodInfo methodInfo) {
                // 设置请求URL和请求方法
                Annotation[] annotations = method.getAnnotations();
                Arrays.stream(annotations).forEach(annotation -> {
                    // GET请求
                    if (annotation instanceof GetMapping) {
                        GetMapping a = (GetMapping) annotation;
                        methodInfo.setUrl(a.value()[0]);
                        methodInfo.setMethod(HttpMethod.GET);
                        methodInfo.setAcceptMediaType(MediaType.parseMediaType(a.consumes()[0]));
                        // POST
                    } else if (annotation instanceof PostMapping) {
                        PostMapping a = (PostMapping) annotation;

                        methodInfo.setUrl(a.value()[0]);
                        methodInfo.setMethod(HttpMethod.POST);
                        methodInfo.setAcceptMediaType(MediaType.parseMediaType(a.consumes()[0]));

                        // DELETE
                    } else if (annotation instanceof DeleteMapping) {
                        DeleteMapping a = (DeleteMapping) annotation;
                        methodInfo.setUrl(a.value()[0]);
                        methodInfo.setMethod(HttpMethod.DELETE);
                        methodInfo.setAcceptMediaType(MediaType.parseMediaType(a.consumes()[0]));
                    }
                });
            }
        });
    }


    /**
     * 获取服务器信息
     *
     * @param type 接口类型
     * @return 服务器信息
     */
    private ServerInfo extractServerInfo(Class<?> type) {
        ApiServer annotation = type.getAnnotation(ApiServer.class);
        return ServerInfo.builder().url(annotation.value()).build();
    }
}

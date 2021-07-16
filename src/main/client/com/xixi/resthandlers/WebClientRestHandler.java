package com.xixi.resthandlers;

import com.xixi.dto.MethodInfo;
import com.xixi.dto.ServerInfo;
import com.xixi.interfaces.RestHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

/**
 * 基于Spring的web client实现rest请求
 *
 * @author markingWang
 * @date 2021/7/16 6:41 下午
 */
public class WebClientRestHandler implements RestHandler {

    private WebClient webClient;

    private RequestBodySpec request;

    @Override
    public void init(ServerInfo serverInfo) {
        this.webClient = WebClient.create(serverInfo.getUrl());
    }

    @Override
    public Object invokeRest(MethodInfo methodInfo) {
        request = webClient
                // 请求方法
                .method(methodInfo.getMethod())
                // 请求url
                .uri(methodInfo.getUrl(), methodInfo.getParams())
                // 请求accept MediaType
                .accept(methodInfo.getAcceptMediaType());

        ResponseSpec retrieve = null;
        // 判断请求是否带了body
        if (methodInfo.getBody() != null) {
            retrieve = request
                    .body(methodInfo.getBody(), methodInfo.getBodyElementType())
                    .retrieve();
        }else {
            retrieve = request.retrieve();
        }

        // 处理异常
        retrieve.onStatus(httpStatus -> httpStatus == HttpStatus.NOT_FOUND,
                response -> Mono.just(new RuntimeException("NOT FOUND")));

        // 返回结果
        Object result = null;
        if (methodInfo.isReturnFlux()) {
            result = retrieve.bodyToFlux(methodInfo.getReturnElementType());
        } else {
            result = retrieve.bodyToMono(methodInfo.getReturnElementType());
        }
        return result;
    }
}

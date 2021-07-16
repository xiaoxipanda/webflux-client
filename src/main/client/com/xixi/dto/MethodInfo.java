package com.xixi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 请求方法信息
 *
 * @author markingWang
 * @date 2021/7/16 5:43 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {

    /**
     * 请求url
     */
    private String url;

    /**
     * 请求方法
     */
    private HttpMethod method;

    /**
     * 请求参数
     */
    private Map<String, Object> params;

    /**
     * 请求body
     */
    private Mono<?> body;

    /**
     * 请求body的类型
     */
    private Class<?> bodyElementType;

    /**
     * 返回类型是否为Flux或者Mono
     */
    private boolean isReturnFlux;

    /**
     * 返回对象类型
     */
    private Class<?> returnElementType;

    /**
     * 接收的MediaType
     */
    private MediaType acceptMediaType = MediaType.APPLICATION_JSON;
}

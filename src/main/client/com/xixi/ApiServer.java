package com.xixi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务器相关信息注解
 *
 * @author markingWang
 * @date 2021/7/16 5:25 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiServer {
    String value() default "";
}

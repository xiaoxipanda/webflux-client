package com.xixi.webfluxclient.configuration;

import com.xixi.interfaces.ProxyCreator;
import com.xixi.proxys.JdkProxyCreator;
import com.xixi.webfluxclient.webclient.IUserServiceApi;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 代理配置bean
 *
 * @author markingWang
 * @date 2021/7/16 5:30 下午
 */
@Configuration
public class ProxyConfiguration {

    @Bean
    ProxyCreator jdkProxyCreator() {
        return new JdkProxyCreator();
    }

    @Bean
    FactoryBean<IUserServiceApi> userServiceApi(ProxyCreator proxyCreator) {
        return new FactoryBean<IUserServiceApi>() {
            @Override
            public IUserServiceApi getObject() throws Exception {
                return (IUserServiceApi) proxyCreator.createProxy(getObjectType());
            }

            @Override
            public Class<?> getObjectType() {
                return IUserServiceApi.class;
            }
        };
    }
}

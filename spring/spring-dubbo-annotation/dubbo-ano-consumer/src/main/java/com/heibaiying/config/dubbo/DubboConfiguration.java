package com.heibaiying.config.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : heibaiying
 * @description : dubbo 配置类
 */
@Configuration
public class DubboConfiguration {

    /**
     * 消费方应用名，用于计算依赖关系，不是匹配条件，不要与提供方一样
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("dubbo-ano-consumer");
        return applicationConfig;
    }

    /**
     * 设置调用服务超时时间
     * 关闭所有服务的启动时检查
     */
    @Bean
    public ConsumerConfig consumerConfig() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(3000);
        consumerConfig.setCheck(false);
        return consumerConfig;
    }

    /**
     * 使用zookeeper注册中心暴露发现服务地址
     */
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://127.0.0.1:2181");
        registryConfig.setClient("curator");
        return registryConfig;
    }

}

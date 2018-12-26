package com.heibaiying.rabbit.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author : heibaiying
 * @description : rabbit 属性配置
 */
@Data
@PropertySource(value = "classpath:rabbitmq.properties")
@Configuration
public class RabbitProperty {


    @Value("${rabbitmq.addresses}")
    private String addresses;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.virtualhost}")
    private String virtualhost;
}

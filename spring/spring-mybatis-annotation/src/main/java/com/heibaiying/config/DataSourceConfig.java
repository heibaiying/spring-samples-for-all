package com.heibaiying.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;

/**
 * @author : heibaiying
 * @description :
 */

@Configuration
@PropertySource(value = "classpath:mysql.properties")
@Data
@NoArgsConstructor
public class DataSourceConfig {

    /**
     * 感觉这种注入的方式并不够好
     * 没有spring-boot中使用@ConfigurationProperties(prefix = "config")指定前缀注入的方式优雅
     */
    @Value("${mysql.driverClassName}")
    private String driverClassName;
    @Value("${mysql.url}")
    private String url;
    @Value("${mysql.username}")
    private String username;
    @Value("${mysql.password}")
    private String password;

}

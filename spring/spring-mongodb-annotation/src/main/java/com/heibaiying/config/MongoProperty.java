package com.heibaiying.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author : heibaiying
 * @description : Mongo 配置属性
 */
@Data
@Configuration
@PropertySource(value = "classpath:mongodb.properties")
public class MongoProperty {

    @Value("${mongo.host}")
    private String host;
    @Value("${mongo.port}")
    private int port;
    @Value("${mongo.dbname}")
    private String dbname;
    @Value("${mongo.connectionsPerHost}")
    private int connectionsPerHost;
    @Value("${mongo.threadsAllowedToBlockForConnectionMultiplier}")
    private int multiplier;
    @Value("${mongo.connectTimeout}")
    private int connectTimeout;
    @Value("${mongo.maxWaitTime}")
    private int maxWaitTime;
    @Value("${mongo.socketKeepAlive}")
    private boolean socketKeepAlive;
    @Value("${mongo.socketTimeout}")
    private int socketTimeout;
}

package com.heibaiying.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * @author : heibaiying
 * @description : Mongo 配置类
 */

@Configuration
@ComponentScan(value = "com.heibaiying.*")
public class MongoConfig {

    @Bean
    public MongoDbFactory mongoDbFactory(MongoProperty mongo) {
        MongoClientOptions options = MongoClientOptions.builder()
                .threadsAllowedToBlockForConnectionMultiplier(mongo.getMultiplier())
                .connectionsPerHost(mongo.getConnectionsPerHost())
                .connectTimeout(mongo.getConnectTimeout())
                .maxWaitTime(mongo.getMaxWaitTime())
                .socketTimeout(mongo.getSocketTimeout())
                .build();
        MongoClient client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getPort()), options);
        return new SimpleMongoDbFactory(client, mongo.getDbname());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }
}

package com.heibaiying.springboot.config;

import com.heibaiying.springboot.constant.Topic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : heibaiying
 * @description : kafka配置类
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic groupTopic() {
        // 指定主题名称，分区数量，和复制因子
        return new NewTopic(Topic.GROUP, 10, (short) 2);
    }

}

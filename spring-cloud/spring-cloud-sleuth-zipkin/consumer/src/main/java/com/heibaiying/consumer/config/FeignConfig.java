package com.heibaiying.consumer.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author : heibaiying
 * @description : feign 配置
 */
@Configuration
public class FeignConfig {

    @Bean
    public Retryer retryer(){
        //重试间隔为 100ms，最大重试时间为 1s, 重试次数为 5 次
        return new Retryer.Default(100,SECONDS.toMillis(1),5);
    }
}

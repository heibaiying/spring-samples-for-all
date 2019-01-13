package com.heibaiying.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 1800) //开启redis session支持,并配置session过期时间
public class SpringBootSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSessionApplication.class, args);
    }

}


package com.heibaiying.dubboconsumer;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo //开启dubbo的注解支持
public class BootDubboConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootDubboConsumerApplication.class, args);
    }

}


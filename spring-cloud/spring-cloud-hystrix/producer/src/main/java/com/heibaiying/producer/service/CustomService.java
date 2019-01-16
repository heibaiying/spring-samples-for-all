package com.heibaiying.producer.service;

import com.heibaiying.producer.service.api.ICustomService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author : heibaiying
 */
@Service
public class CustomService implements ICustomService {

    @Autowired
    RestTemplate restTemplate;

    @Override
    @HystrixCommand(fallbackMethod = "queryCustomsFail")
    public String queryCustoms() {
        return restTemplate.getForObject("http://consumer/consumers", String.class);
    }

    public String queryCustomsFail() {
        return "获取用户列表失败";
    }

}

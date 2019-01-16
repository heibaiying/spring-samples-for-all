package com.heibaiying.producer.controller;

import com.heibaiying.producer.service.api.ICustomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : heibaiying
 * @description : 用来测试turbine聚合监控功能的接口 调用了custom的服务
 */
@RestController
public class CustomController {

    @Autowired
    private ICustomService customService;

    @GetMapping("consumers")
    public String queryCustoms() {
        return customService.queryCustoms();
    }
}

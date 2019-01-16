package com.heibaiying.consumer.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : heibaiying
 */
@RestController
public class CustomController {

    @GetMapping("consumers")
    public String queryCustoms() {
        return "用户产品偏好列表生成中,请月底再获取";
    }
}

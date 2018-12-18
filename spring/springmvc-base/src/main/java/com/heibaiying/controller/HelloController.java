package com.heibaiying.controller;

import com.heibaiying.exception.NoAuthException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author : heibaiying
 * @description : hello spring
 */

@Controller
@RequestMapping("mvc")
public class HelloController {

    @RequestMapping("hello")
    private String hello() {
        return "hello";
    }


    @RequestMapping("auth")
    private void auth() {
        throw new NoAuthException("没有对应的访问权限！");
    }
}

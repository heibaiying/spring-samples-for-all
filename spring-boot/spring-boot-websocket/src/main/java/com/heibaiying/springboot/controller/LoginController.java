package com.heibaiying.springboot.controller;

import com.heibaiying.springboot.constant.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

/**
 * @author : 罗祥
 * @description : 简单登录
 * @date :create in 2018/12/27
 */
@Controller
public class LoginController {

    @PostMapping("login")
    public String login(String username, HttpSession session) {
        session.setAttribute(Constant.USER_NAME, username);
        return "chat";
    }

    @GetMapping
    public String index() {
        return "index";
    }
}

package com.heibaiying.springboot.controller;

import com.heibaiying.springboot.constant.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

/**
 * @description : 简单登录
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

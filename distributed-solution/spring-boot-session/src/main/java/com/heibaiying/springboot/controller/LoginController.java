package com.heibaiying.springboot.controller;


import com.heibaiying.springboot.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @author : heibaiying
 * @description : 登录
 */
@Controller
public class LoginController {

    @RequestMapping
    public String index() {
        return "index";
    }

    @RequestMapping("home")
    public String home() {
        return "home";
    }

    @PostMapping("login")
    public String login(User user, HttpSession session) {
        // 随机生成用户id
        user.setUserId(Math.round(Math.floor(Math.random() * 10 * 1000)));
        // 将用户信息保存到id中
        session.setAttribute("USER", user);
        return "home";
    }

}

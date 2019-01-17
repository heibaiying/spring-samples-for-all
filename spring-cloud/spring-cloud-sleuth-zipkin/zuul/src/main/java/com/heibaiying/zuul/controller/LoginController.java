package com.heibaiying.zuul.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Random;

/**
 * @author : heibaiying
 * @description : 登录
 */
@Controller
public class LoginController {

    @RequestMapping("index")
    public String login(){
        return "index";
    }

    @RequestMapping("login")
    public String login(String username,HttpSession session){
        session.setAttribute("code",username+String.valueOf(new Random().nextInt(10*1000)));
        return "redirect:/consumer/sell/products";
    }
}

package com.heibaiying.controller;

import com.heibaiying.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author : heibaiying
 * @description : 登录
 */
@Controller
public class LoginController {

    @RequestMapping
    public String index(){
        return "index";
    }

    @RequestMapping("home")
    public String home(){
        return "home";
    }

    @PostMapping("login")
    public String login(User user, HttpSession session, HttpServletRequest request, Model model){
        // 随机生成用户id
        user.setUserId(Math.round(Math.floor(Math.random() *10*1000)));
        // 将用户信息保存到id中
        session.setAttribute("USER",user);
        return "redirect:home";
    }

}

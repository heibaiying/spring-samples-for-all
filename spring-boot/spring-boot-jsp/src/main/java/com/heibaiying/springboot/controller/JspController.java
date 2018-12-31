package com.heibaiying.springboot.controller;

import com.heibaiying.springboot.bean.Programmer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

/**
 * @author : heibaiying
 */

@Controller
@RequestMapping("index")
public class JspController {

    @RequestMapping
    public String jsp(Model model){
        Programmer programmer = new Programmer("heibai", 21, 1298.31f, LocalDate.now());
        model.addAttribute("programmer",programmer);
        return "show";
    }
}

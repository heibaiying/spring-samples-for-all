package com.heibaiying.springboot.controller;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.dao.ProgrammerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : heibaiying
 */

@RestController
public class ProgrammerController {

    @Autowired
    private ProgrammerDao programmerDao;

    @GetMapping("/programmers")
    public List<Programmer> get() {
        return programmerDao.selectAll();
    }
}

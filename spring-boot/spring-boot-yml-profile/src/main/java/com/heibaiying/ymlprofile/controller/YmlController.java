package com.heibaiying.ymlprofile.controller;

import com.heibaiying.ymlprofile.config.Programmer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : heibaiying
 */

@RestController
@RequestMapping("yml")
public class YmlController {

    @Autowired
    private Programmer programmer;

    @RequestMapping
    public Programmer programmer(){
        return programmer;
    }
}

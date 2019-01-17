package com.heibaiying.configclient.controller;


import com.heibaiying.configclient.config.Programmer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : heibaiying
 */

@RestController
public class ConfigController {

    @Autowired
    private Programmer programmer;

    @RequestMapping("programmer")
    public String getProgrammer() {
        return programmer.toString();
    }
}

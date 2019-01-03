package com.heibaiying.springboot.controller;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : heibaiying
 * @description :在 Spring Boot 中可以通过 HTTP 接口将 Druid 监控数据以JSON 的形式暴露出去，
 * 实际使用中你可以根据你的需要自由地对监控数据、暴露方式进行扩展。
 */

@RestController
public class DruidStatController {

    @GetMapping("/stat")
    public Object druidStat() {
        // DruidStatManagerFacade#getDataSourceStatDataList 该方法可以获取所有数据源的监控数据
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}

package com.heibaiying.configclient.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "programmer")
@Data
@ToString
@RefreshScope // 定义下面配置热刷新范围
public class Programmer{

    private String name;
    private int age;
    private boolean married;
    private Date hireDate;
    private float salary;
    private int random;
    private Map<String, String> skill;
    private List company;
    private School school;

}
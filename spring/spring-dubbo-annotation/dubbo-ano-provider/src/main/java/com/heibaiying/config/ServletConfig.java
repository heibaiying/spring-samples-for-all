package com.heibaiying.config;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author : heibaiying
 * @description : 主配置类
 */
@Configuration
@EnableWebMvc
@DubboComponentScan(basePackages = "com.heibaiying.service") // 扫描dubbo注解
@ComponentScan(basePackages = {"com.heibaiying.*"})
public class ServletConfig implements WebMvcConfigurer {

}

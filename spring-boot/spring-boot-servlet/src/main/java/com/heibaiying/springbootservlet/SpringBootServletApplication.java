package com.heibaiying.springbootservlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ServletComponentScan("com.heibaiying.springbootservlet") //在独立的容器（非内嵌）中@ServletComponentScan不起作用，可以不配置，取为代之的是容器内建的discovery机制自动发现。
public class SpringBootServletApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        //传入SpringBoot应用的主程序
        return application.sources(SpringBootServletApplication.class);
    }

    /**
     * 如果用外置tomcat,启动报错java.lang.NoClassDefFoundError: javax/el/ELManager
     * 是因为tomcat 7.0 el-api包中没有ELManager类 , 切换tomcat 为8.0 以上版本即可
    */

    public static void main(String[] args) {
        SpringApplication.run(SpringBootServletApplication.class, args);
    }

}


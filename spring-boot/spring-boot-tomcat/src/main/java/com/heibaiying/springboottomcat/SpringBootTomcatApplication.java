package com.heibaiying.springboottomcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * 如果用外置tomcat,启动报错java.lang.NoClassDefFoundError: javax/el/ELManager
 * 是因为tomcat 7.0 el-api包中没有ELManager类 , 切换tomcat 为8.0 以上版本即可
 */
@SpringBootApplication
public class SpringBootTomcatApplication extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        //传入SpringBoot应用的主程序
        return application.sources(SpringBootTomcatApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTomcatApplication.class, args);
    }

}


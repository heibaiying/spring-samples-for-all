package com.heibaiying.config;

import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;

/**
 * @author : heibaiying
 * @description :
 */

@Configuration
@ComponentScan(value = "com.heibaiying.email")
public class EmailConfig {

    /***
     * 在这里可以声明不同的邮件服务器主机，通常是SMTP主机,而具体的用户名和时授权码则建议在业务中从数据库查询
     */
    @Bean(name = "qqMailSender")
    JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPassword("587");
        return javaMailSender;
    }

    /***
     * 配置模板引擎
     */
    @Bean
    GroupTemplate groupTemplate() throws IOException {
        //指定加载模板资源的位置 指定在classpath:beetl下-
        ClasspathResourceLoader loader = new ClasspathResourceLoader("beetl");
        //beetl配置 这里采用默认的配置-
        org.beetl.core.Configuration configuration = org.beetl.core.Configuration.defaultConfiguration();
        return new GroupTemplate(loader, configuration);
    }
}

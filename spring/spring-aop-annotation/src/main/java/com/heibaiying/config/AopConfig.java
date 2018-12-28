package com.heibaiying.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author : heibaiying
 * @description : 开启切面配置
 */
@Configuration
@ComponentScan("com.heibaiying.*")
@EnableAspectJAutoProxy // 开启@Aspect注解支持 等价于<aop:aspectj-autoproxy>
public class AopConfig {
}

package com.heibaiying.springbootservlet.config;

import com.heibaiying.springbootservlet.filter.CustomFilter;
import com.heibaiying.springbootservlet.listen.CustomListen;
import com.heibaiying.springbootservlet.servlet.CustomServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

/**
 * @author : heibaiying
 */
@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean registrationBean() {
        return new ServletRegistrationBean<HttpServlet>(new CustomServlet(), "/servlet");
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean<Filter>();
        bean.setFilter(new CustomFilter());
        bean.addUrlPatterns("/servlet");
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean listenerRegistrationBean() {
        return new ServletListenerRegistrationBean<ServletContextListener>(new CustomListen());
    }

}

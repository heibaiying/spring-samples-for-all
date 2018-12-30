package com.heibaiying.springbootservlet.listen;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author : heibaiying
 * @description : 自定义监听器
 */
public class CustomListen implements ServletContextListener {

    //Web应用程序初始化过程正在启动的通知
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("容器初始化启动");
    }



    /* 通知servlet上下文即将关闭
     * 这个地方如果我们使用的是spring boot 内置的容器 是监听不到销毁过程，所以我们使用了外置 tomcat 容器
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("容器即将销毁");
    }
}

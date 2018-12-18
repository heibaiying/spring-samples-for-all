# springmvc基础（基于注解）

**开发环境：IDEA**

**spring版本：5.1.3.RELEASE**

**本部分内容官方文档链接：[Web Servlet](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/web.html#spring-web)**

## 一、搭建hello spring工程

### 1.1 项目搭建

1.新建maven web工程，并引入相应的依赖

```xml
 <properties>
        <spring-base-version>5.1.3.RELEASE</spring-base-version>
 </properties>

<dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring-base-version}</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```

2.得益于servlet3.0和spring的支持，我们可以在没有web.xml的情况下完成关于servlet配置。

   新建DispatcherServletInitializer.java文件,这个类的作用相当于我们在xml方式下web.xml中配置的DispatcherServlet

```java
package com.heibaiying.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @author : heibaiying
 */

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{ServletConfig.class};
    }

    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}

```

3.新建ServletConfig.java，文件内容如下(这个类相当于我们在xml配置方式中的springApplication.xml)

```java
package com.heibaiying.config;

import com.heibaiying.exception.NoAuthExceptionResolver;
import com.heibaiying.interceptors.MyFirstInterceptor;
import com.heibaiying.interceptors.MySecondInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

/**
 * @author : heibaiying
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.heibaiying.controller"})
public class ServletConfig implements WebMvcConfigurer {

    /**
     * 配置视图解析器
     */
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
        internalResourceViewResolver.setSuffix(".jsp");
        internalResourceViewResolver.setExposeContextBeansAsAttributes(true);
        return internalResourceViewResolver;
    }

    /**
     * 配置静态资源处理器
     */
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}

```

4.在src 下新建controller用于测试

```java
package com.heibaiying.controller;

import com.heibaiying.exception.NoAuthException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : heibaiying
 * @description : hello spring
 */

@Controller
@RequestMapping("mvc")
public class HelloController {

    @RequestMapping("hello")
    private String hello() {
        return "hello";
    }

}

```

5.在WEB-INF 下新建jsp文件夹，新建hello.jsp 文件

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    Hello Spring MVC!
</body>
</html>
```

6.启动tomcat服务，访问localhost:8080/mvc/hello

### 1.2 相关注解说明

**1.@Configuration**

@Configuration用于定义配置类，可替换xml配置文件，被注解的类内部包含有一个或多个被@Bean注解的方法，这些方法将会被AnnotationConfigApplicationContext或AnnotationConfigWebApplicationContext类进行扫描，并用于构建bean定义，初始化Spring容器。

**2.@EnableWebMvc**

简单的说就是提供了部分springmvc的功能，例如格式转换和参数绑定。



## 二、配置自定义拦截器

1.创建自定义拦截器，实现接口HandlerInterceptor（这里我们创建两个拦截器，用于测试拦截器方法的执行顺序）

```java
package com.heibaiying.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : heibaiying
 * @description : spring5 中 preHandle，postHandle，afterCompletion 在接口中被声明为默认方法
 */
public class MyFirstInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("进入第一个拦截器preHandle");
        return true;
    }

    // 需要注意的是，如果对应的程序报错，不一定会进入这个方法 但一定会进入afterCompletion这个方法
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        System.out.println("进入第一个拦截器postHandle");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("进入第一个拦截器afterCompletion");
    }
}
```

```java
package com.heibaiying.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : heibaiying
 * @description : spring5 中 preHandle，postHandle，afterCompletion 在接口中被声明为默认方法
 */
public class MySecondInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("进入第二个拦截器preHandle");
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        System.out.println("进入第二个拦截器postHandle");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("进入第二个拦截器afterCompletion");
    }
}

```

2.在ServletConfig.java中注册自定义拦截器

```java
 /**
  * 添加自定义拦截器
  */
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyFirstInterceptor()).addPathPatterns("/mvc/**").excludePathPatterns("mvc/login");
        registry.addInterceptor(new MySecondInterceptor()).addPathPatterns("/mvc/**");
    }
```

3.关于多个拦截器方法执行顺序的说明

拦截器的执行顺序是按声明的先后顺序执行的，先声明的拦截器中的preHandle方法会先执行，然而它的postHandle方法和afterCompletion方法却会后执行。



## 三、全局异常处理 

1.定义自定义异常

```java
package com.heibaiying.exception;

/**
 * @author : heibaiying
 * @description : 自定义无权限异常
 */
public class NoAuthException extends RuntimeException {

    public NoAuthException() {
        super();
    }

    public NoAuthException(String message) {
        super(message);
    }

    public NoAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAuthException(Throwable cause) {
        super(cause);
    }

}

```

2.实现自定义异常处理器

```java
package com.heibaiying.exception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : heibaiying
 * @description : 无权限异常处理机制
 */
public class NoAuthExceptionResolver implements HandlerExceptionResolver {

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof NoAuthException && !isAjax(request)) {
            return new ModelAndView("NoAuthPage");
        }
        return new ModelAndView();
    }

    // 判断是否是Ajax请求
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}

```

3.在ServletConfig.java注册自定义异常处理器

```java
/**
* 添加全局异常处理器
*/
public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    resolvers.add(new NoAuthExceptionResolver());
}
```

4.定义测试controller，抛出自定义异常

```java
@Controller
@RequestMapping("mvc")
public class HelloController {

    @RequestMapping("hello")
    private String hello() {
        return "hello";
    }


    @RequestMapping("auth")
    private void auth() {
        throw new NoAuthException("没有对应的访问权限！");
    }
}
```

注：调用这个controller时，同时也可以验证在拦截器部分提到的：如果对应的程序报错，拦截器不一定会进入postHandle这个方法 但一定会进入afterCompletion这个方法
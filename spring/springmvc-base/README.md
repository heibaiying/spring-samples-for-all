# springmvc基础（基于xml配置）

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

2.配置web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
		 http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <!--配置spring前端控制器-->
    <servlet>
        <servlet-name>springMvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:springApplication.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>springMvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

</web-app>
```

3.在resources下新建springApplication.xml文件，文件内容如下:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd
        http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.1.xsd">

    <!-- 开启注解包扫描-->
    <context:component-scan base-package="com.heibaiying.*"/>

    <!--使用默认的Servlet来响应静态文件 详见 1.2 -->
    <mvc:default-servlet-handler/>

    <!-- 开启注解驱动 详见 1.2 -->
    <mvc:annotation-driven/>

    <!-- 配置视图解析器 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
          id="internalResourceViewResolver">
        <!-- 前缀 -->
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <!-- 后缀 -->
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>
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

### 1.2 相关配置讲解

**1.\<mvc:default-servlet-handler/>**

在web.xml配置中，我们将DispatcherServlet的拦截路径设置为“\”，则spring会捕获所有web请求，包括对静态资源的请求，为了正确处理对静态资源的请求，spring提供了两种解决方案：

- 配置\<mvc:default-servlet-handler/>  ： 配置<mvc:default-servlet-handler />后，会在Spring MVC上下文中定义一个org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler，它会对进入DispatcherServlet的URL进行筛查，如果发现是静态资源的请求，就将该请求转由Web应用服务器默认的Servlet处理，如果不是静态资源的请求，才由DispatcherServlet继续处理。

- 配置\<mvc:resources /> ：指定静态资源的位置和路径映射：

  ```xml
  <mvc:resources location="/img/" mapping="/img/**"/>   
  <mvc:resources location="/js/" mapping="/js/**"/>    
  <mvc:resources location="/css/" mapping="/css/**"/>  
  ```

**2.\<mvc:annotation-driven/>**

<mvc:annotation-driven /> 会自动注册DefaultAnnotationHandlerMapping与AnnotationMethodHandlerAdapter 
两个bean,用以支持@Controllers分发请求。并提供了数据绑定、参数转换、json转换等功能，所以必须加上这个配置。



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

2.在springApplication.xml中注册自定义拦截器

```xml
    <!--配置拦截器-->
    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/mvc/**"/>
            <mvc:exclude-mapping path="/mvc/login"/>
            <bean class="com.heibaiying.interceptors.MyFirstInterceptor"/>
        </mvc:interceptor>
        <mvc:interceptor>
            <mvc:mapping path="/mvc/**"/>
            <bean class="com.heibaiying.interceptors.MySecondInterceptor"/>
        </mvc:interceptor>
    </mvc:interceptors>
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

3.在springApplication.xml注册自定义异常处理器

```xml
<!--配置全局异常处理器-->
<bean class="com.heibaiying.exception.NoAuthExceptionResolver"/>
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
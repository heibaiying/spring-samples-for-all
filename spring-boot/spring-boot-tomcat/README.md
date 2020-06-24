# Spring Boot 整合 Tomcat

<nav>
<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-基本依赖">1.2 基本依赖</a><br/>
<a href="#二整合-Tomcat">二、整合 Tomcat</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-SpringBootServletInitializer">2.1 SpringBootServletInitializer</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-定义视图">2.2 定义视图</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-整合测试">2.3 整合测试</a><br/>
</nav>

## 一、项目说明

### 1.1 项目结构

Spring Boot 默认采用内置的 Web 容器，因此打成 JAR 包后就可以直接运行。但在某的时候，你可能还是需要使用 Tomcat 来运行和管理 Web 项目，因此本用例主要介绍 Spring Boot 与 Tomcat 的整合方式。另外 Spring Boot 内置的 Web 容器默认并不支持 JSP，所以可以使用跳转到 JSP 页面的方式来测试整合外部容器是否成功。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-boot-tomcat.png"/> </div>

### 1.2 基本依赖

```xml
<!--指定打包方式--> 
<packaging>war</packaging>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!--排除内置容器依赖 使用外部 tomcat 容器启动-->
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <!--使用外置容器时候 SpringBootServletInitializer 依赖此包 -->
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <version>2.5</version>
    <scope>provided</scope>
</dependency>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <configuration>
                <!--不需要检查web.xml是否存在-->
                <failOnMissingWebXml>false</failOnMissingWebXml>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 二、整合 Tomcat

### 2.1 SpringBootServletInitializer

修改启动类，继承自 SpringBootServletInitializer，并覆盖重写其中 configure 方法：

```java
/**
 * 如果用外置 tomcat,启动报错 java.lang.NoClassDefFoundError: javax/el/ELManager
 * 是因为 tomcat 7.0 el-api 包中没有 ELManager 类 , 切换 tomcat 为 8.0 以上版本即可
 */
@SpringBootApplication
public class SpringBootTomcatApplication extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        //传入 SpringBoot 应用的主程序
        return application.sources(SpringBootTomcatApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTomcatApplication.class, args);
    }

}
```

### 2.2 定义视图

在 application.yml 中指定访问视图文件的前缀和后缀：

```yml
spring:
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp
```

### 2.3 整合测试

新建 controller 和 show.jsp 测试整合是否成功：

```java
@Controller
@RequestMapping("index")
public class JspController {

    @RequestMapping
    public String jsp(Model model){
        Programmer programmer = new Programmer("heibai", 21, 1298.31f, LocalDate.now());
        model.addAttribute("programmer",programmer);
        return "show";
    }
}
```

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>programmer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/show.css">
</head>
<body>
<ul>
    <li>姓名: ${programmer.name}</li>
    <li>年龄: ${programmer.age}</li>
</ul>
</body>
</html>
```


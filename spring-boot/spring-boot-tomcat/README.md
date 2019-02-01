# spring boot 整合 tomcat
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目主要依赖">1.2 项目主要依赖</a><br/>
<a href="#二整合-tomcat">二、整合 tomcat</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-修改启动类继承自SpringBootServletInitializer并覆盖重写其中configure方法">2.1 修改启动类，继承自SpringBootServletInitializer，并覆盖重写其中configure方法</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-在applicationyml-中指定访问视图文件的前缀和后缀">2.2 在application.yml 中指定访问视图文件的前缀和后缀 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-新建controller和showjsp-测试整合是否成功">2.3 新建controller和show.jsp 测试整合是否成功</a><br/>
## 正文<br/>

## 一、说明

#### 1.1 项目结构说明

spring boot 整合 tomcat 后支持jsp 的使用（内置容器默认是不支持jsp），所以项目整合后采用jspController 跳转到show.jsp测试整合是否成功。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-tomcat.png"/> </div>

#### 1.2 项目主要依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!--排除内置容器依赖 使用外部tomcat容器启动-->
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <!--使用外置容器时候SpringBootServletInitializer 依赖此包 -->
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <version>2.5</version>
    <scope>provided</scope>
</dependency>
```

## 二、整合 tomcat

#### 2.1 修改启动类，继承自SpringBootServletInitializer，并覆盖重写其中configure方法

```java
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
```

#### 2.2 在application.yml 中指定访问视图文件的前缀和后缀 

```yml
spring:
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp
```

#### 2.3 新建controller和show.jsp 测试整合是否成功

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


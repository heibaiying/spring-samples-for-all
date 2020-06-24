# Spring Boot 整合 JSP
<nav>
<a href="#一项目说明">一、项目说明</a><br/>
<a href="#二整合-JSP">二、整合 JSP</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-基本依赖">2.1 基本依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-配置视图">2.2 配置视图</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23--整合测试">2.3  整合测试</a><br/>
</nav>

## 一、项目说明

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-boot-jsp.png"/> </div>


## 二、整合 JSP

### 2.1 基本依赖

导入整合所需的依赖：

```xml
<!--整合 jsp 依赖包-->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <scope>provided</scope>
</dependency>
<!--jsp jstl 标签支持-->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
</dependency>
```

### 2.2 配置视图

在 application.yml 中指定访问视图文件的前缀和后缀 ：

```yml
spring:
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp
```

### 2.3  整合测试

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


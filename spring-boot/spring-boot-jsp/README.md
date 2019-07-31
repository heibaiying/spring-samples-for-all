# spring boot 内置容器 整合 jsp

## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目主要依赖">1.2 项目主要依赖</a><br/>
<a href="#二整合-jsp">二、整合 jsp</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-导入整合的依赖">2.1 导入整合的依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-在applicationyml-中指定访问视图文件的前缀和后缀">2.2 在application.yml 中指定访问视图文件的前缀和后缀 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23--新建controller和showjsp-测试整合是否成功">2.3  新建controller和show.jsp 测试整合是否成功</a><br/>
## 正文<br/>




## 一、说明

#### 1.1 项目结构

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-jsp.png"/> </div>

#### 1.2 项目主要依赖

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

## 二、整合 jsp

#### 2.1 导入整合的依赖

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

#### 2.2 在application.yml 中指定访问视图文件的前缀和后缀 

```yml
spring:
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp
```

#### 2.3  新建controller和show.jsp 测试整合是否成功

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


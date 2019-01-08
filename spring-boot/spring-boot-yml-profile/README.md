# spring-boot 基础

## 一、说明

#### 1.1 项目结构说明

1. 本项目搭建一个简单的hello spring 的 web工程，简单说明spring-boot 的开箱即用的特性；
2. 模板引擎采用freemaker 和 thymeleaf 作为示例，分别对应模板文件makershow.ftl 和 leafShow.html；
3. spring boot 2.x 默认是不支持jsp的，需要额外的配置，关于使用jsp的整合可以参考spring-boot-jsp项目。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-base.png"/> </div>

#### 1.2 项目依赖

导入相关的starter(启动器)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.heibaiying</groupId>
    <artifactId>spring-boot-base</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-boot-base</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!--模板引擎-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-freemarker</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <!--web 启动器-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--lombok 插件-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!--测试相关依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <!--Spring Boot的Maven插件（Spring Boot Maven plugin）能够以Maven的方式为应用提供Spring Boot的支持-->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```

1. spring boot 项目默认继承自spring-boot-starter-parent，而spring-boot-starter-parent继承自spring-boot-dependencies,  spring-boot-dependencies中定义了关于spring boot 依赖的各种jar包的版本，是spring boot 的版本管理中心。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-dependencies.png"/> </div>

2. 关于spring boot 2.x官方支持的所有starter 可以参见官方文档 [Table 13.1. Spring Boot application starters](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#using-boot-starter)



## 二、spring boot 主启动类

 如果采用IDEA 或者 Spring Tool Suite (STS) 等开发工具创建的spring boot 工程，会默认创建启动类，如果没有创建，需要手动创建启动类

```java
package com.heibaiying.springbootbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootBaseApplication {

    // 启动类默认开启包扫描，扫描与主程序所在包及其子包，对于本工程而言 默认扫描 com.heibaiying.springbootbase
    public static void main(String[] args) {
        SpringApplication.run(SpringBootBaseApplication.class, args);
    }

}
```

@SpringBootApplication 注解是一个复合注解,里面包含了@ComponentScan注解，默认开启包扫描，扫描与主程序所在包及其子包，对于本工程而言 默认扫描 com.heibaiying.springbootbase

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
		@Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
    ...
}
```



## 三、开箱即用的web工程

在springbootBaseApplication.java 的同级目录创建controller文件夹，并在其中创建RestfulController.java,启动项目访问localhost:8080/restful/programmers 即可看到项目搭建成功。

```java
/**
 * @author : heibaiying
 * @description : restful 控制器
 */
@RestController
@RequestMapping("restful")
public class RestfulController {

    @GetMapping("programmers")
    private List<Programmer> getProgrammers() {
        List<Programmer> programmers = new ArrayList<>();
        programmers.add(new Programmer("xiaoming", 12, 100000.00f, LocalDate.of(2019, Month.AUGUST, 2)));
        programmers.add(new Programmer("xiaohong", 23, 900000.00f, LocalDate.of(2013, Month.FEBRUARY, 2)));
        return programmers;
    }
}
```

这里之所以能够开箱即用，是因为我们在项目中导入spring-boot-starter-web启动器，而@SpringBootApplication 复合注解中默认开启了@EnableAutoConfiguration注解允许开启自动化配置，spring在检查导入starter-web的依赖后就会开启web的自动化配置。



## 四、模板引擎

这里我们在一个项目中同时导入了freemaker 和 thymeleaf的starter（虽然并不推荐，但是在同一个项目中是可以混用这两种模板引擎的）。

#### 4.1 freemarker

```java
/**
 * @author : heibaiying
 * @description : 跳转渲染模板引擎 默认模板的存放位置为classpath:templates
 */
@Controller
@RequestMapping("freemarker")
public class FreeMarkerController {

    @RequestMapping("show")
    private String programmerShow(ModelMap modelMap){
        List<Programmer> programmerList=new ArrayList<>();
        programmerList.add(new Programmer("xiaoming",12,100000.00f,LocalDate.of(2019,Month.AUGUST,2)));
        programmerList.add(new Programmer("xiaohong",23,900000.00f,LocalDate.of(2013,Month.FEBRUARY,2)));
        modelMap.addAttribute("programmers",programmerList);
        return "markerShow";
    }
}

```

```html
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>freemarker模板引擎</title>
</head>
<body>
    <ul>
        <#list programmers as programmer>
           <li>姓名: ${programmer.name} 年龄: ${programmer.age}</li>
        </#list>
    </ul>
</body>
</html>
```

#### 4.2 thymeleaf

```java
/**
 * @author : heibaiying
 * @description : 跳转渲染模板引擎 默认模板的存放位置为classpath:templates
 */
@Controller
@RequestMapping("thymeleaf")
public class ThymeleafController {

    @RequestMapping("show")
    private String programmerShow(ModelMap modelMap) {
        List<Programmer> programmerList = new ArrayList<>();
        programmerList.add(new Programmer("xiaoming", 12, 100000.00f, LocalDate.of(2019, Month.AUGUST, 2)));
        programmerList.add(new Programmer("xiaohong", 23, 900000.00f, LocalDate.of(2013, Month.FEBRUARY, 2)));
        modelMap.addAttribute("programmers", programmerList);
        return "leafShow";
    }
}

```

```html
<!doctype html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>thymeleaf模板引擎</title>
</head>
<body>
    <ul th:each="programmer:${programmers}">
        <li>
            姓名:<span th:text="${programmer.name}"></span>
            薪水:<span th:text="${programmer.salary}"></span>
        </li>
    </ul>
</body>
</html>
```

#### 4.3 文档说明

freemarker：提供了完善的中文文档，地址 http://freemarker.foofun.cn/

thymeleaf：官方英文文档地址：[thymeleaf 3.0.11RELEASE](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.pdf)

注：我在本仓库中也上传了一份[thymeleaf中文文档（gangzi828(刘明刚 译）](https://github.com/heibaiying/spring-samples-for-all/tree/master/referenced%20documents)，翻译的版本为3.0.5RELEASE
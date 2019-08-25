# Spring 实现分布式 Session
<nav>
<a href="#一项目结构">一、项目结构</a><br/>
<a href="#二实现分布式-Session">二、实现分布式 Session</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-基本依赖">2.1 基本依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-Session-拦截器">2.2 Session 拦截器</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-实现原理">2.3 实现原理</a><br/>
<a href="#三验证分布式-Session">三、验证分布式 Session</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-测试准备">3.1 测试准备</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-测试结果">3.2 测试结果</a><br/>
</nav>

## 一、项目结构

分布式 Session 主要配置文件为 spring-session.xml 和 web.xml，其他的配置为标准的 web 工程的配置：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-session.png"/> </div>
## 二、实现分布式 Session

### 2.1 基本依赖

```xml
<!--分布式 session 相关依赖-->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>2.1.3.RELEASE</version>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
    <version>2.1.3.RELEASE</version>
</dependency>
```

### 2.2 Session 拦截器

在 web.xml 中配置 Session 拦截器：

```xml
<!--配置http session-->
<filter>
    <filter-name>springSessionRepositoryFilter</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>springSessionRepositoryFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

```

### 2.3 实现原理

Spring 通过将 Session 信息存储到公共容器中，这样不同的 Web 服务就能共享到相同的 Session 信息，从而实现分布式 Session。Spring 支持使用 Redis， Jdbc，mongodb，Hazelcast 等作为公共的存储容器。这里我们以 Redis 作为公共的存储容器，需要创建配置文件 spring- session.xml，内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd">

    <context:property-placeholder location="classpath:redis.properties"/>


    <!--配置 http session-->
    <bean id="redisHttpSessionConfiguration"
          class="org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration">
        <!--session 有效期 单位秒 每次访问都会刷新有效期-->
        <property name="maxInactiveIntervalInSeconds" value="1800"/>
    </bean>

    <!--单机版本配置 redis 配置-->
    <bean id="redisStandaloneConfiguration"
          class="org.springframework.data.redis.connection.RedisStandaloneConfiguration">
        <constructor-arg name="hostName" value="${redis.host}"/>
        <constructor-arg name="port" value="${redis.port}"/>
    </bean>


    <bean id="jedisConnectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory"
          p:usePool="true">
        <!--单机版本配置-->
        <constructor-arg name="standaloneConfig" ref="redisStandaloneConfiguration"/>
        <!--集群配置-->
        <!--<constructor-arg name="clusterConfig" ref="redisClusterConfiguration"/>-->
    </bean>


    <!--集群配置-->
    <!--<bean id="redisClusterConfiguration" class="org.springframework.data.redis.connection.RedisClusterConfiguration">
        <property name="maxRedirects" value="3"/>
        <constructor-arg>
            <set>
                <value>127.0.0.1:6379</value>
                <value>127.0.0.1:6380</value>
                <value>127.0.0.1:6381</value>
            </set>
        </constructor-arg>
    </bean>-->


    <bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate"
          p:connection-factory-ref="jedisConnectionFactory"/>

</beans>
```

## 三、验证分布式 Session

### 3.1 测试准备

创建测试接口和测试页面：

```java
@Controller
public class LoginController {

    @RequestMapping
    public String index(){
        return "index";
    }

    @RequestMapping("home")
    public String home(){
        return "home";
    }

    @PostMapping("login")
    public String login(User user, HttpSession session, HttpServletRequest request, Model model){
        // 随机生成用户 id
        user.setUserId(Math.round(Math.floor(Math.random() *10*1000)));
        // 将用户信息保存到 id 中
        session.setAttribute("USER",user);
        return "redirect:home";
    }

}
```

登录页面：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录页面</title>
</head>
<body>
<h5>服务器:<%=request.getServerName()+":"+request.getServerPort()%></h5>
<form action="${pageContext.request.contextPath}/login" method="post">
     用户：<input type="text" name="username"><br/>
     密码：<input type="password" name="password"><br/>
    <button type="submit">登录</button>
</form>
</body>
</html>
```

session 信息展示页面 (home.jsp)：

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>主页面</title>
</head>
<body>
    <h5>服务器:<%=request.getServerName()+":"+request.getServerPort()%></h5>
    <h5>登录用户: ${sessionScope.USER.username} </h5>
    <h5>用户编号: ${sessionScope.USER.userId} </h5>
</body>
</html>
```

### 3.2 测试结果

这里采用两个 Tomcat 分别启动项目，在第一个项目的 index.jsp 页面进行登录；第二个项目不登录，直接访问 Session 展示页home.jsp :

Tomcat 1 配置：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-session-tomcat01.png"/> </div>
Tomcat 2 配置：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-session-tomcat02.png"/> </div>
**测试结果：**

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-session-8080.png"/> </div>
<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-session-8090.png"/> </div>

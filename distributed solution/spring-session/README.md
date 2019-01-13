# spring session 实现分布式 session

## 一、项目结构

分布式session 主要配置文件为spring-session.xml和web.xml，其他的配置为标准的web工程的配置。

![spring-cloud-eureka](D:\spring-samples-for-all\pictures\spring-session.png)

## 二、分布式session的配置

#### 2.1 引入依赖

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

#### 2.2 在web.xml中配置session拦截器

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

#### 2.3 创建配置文件spring- session.xml，配置redis连接

有两点需要特别说明：

1. spring-session 不仅提供了redis作为公共session存储的方案，同时也支持jdbc、mongodb、Hazelcast等作为公共session的存储；
2. 对于redis 存储方案而言，官方也提供了不止一种整合方式，这里我们选取的整合方案是jedis客户端作为连接，当然也可以使用Lettuce作为客户端连接。

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

    <!--单机版本配置redis 配置-->
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

## 三、验证分布式session

#### 3.1 创建测试controller和测试页面

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
        // 随机生成用户id
        user.setUserId(Math.round(Math.floor(Math.random() *10*1000)));
        // 将用户信息保存到id中
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

session 信息展示页面(home.jsp)：

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

#### 3.2 启动项目

这里我们采用两个tomcat分别启动项目，在第一个项目index.jsp页面进行登录，第二个项目不登录，直接访问session展示页（home.jsp）

tomcat 1 配置：

![spring-session-tomcat01](D:\spring-samples-for-all\pictures\spring-session-tomcat01.png)

tomcat 2 配置：

![spring-session-tomcat02](D:\spring-samples-for-all\pictures\spring-session-tomcat02.png)

**测试结果：**

![spring-session-8080](D:\spring-samples-for-all\pictures\spring-session-8080.png)

![spring-session-8090](D:\spring-samples-for-all\pictures\spring-session-8090.png)
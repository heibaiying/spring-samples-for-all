# spring boot 实现分布式 session

## 一、项目结构

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-session.png"/> </div>



## 二、分布式session的配置

#### 2.1 引入依赖

```xml
<!--分布式 session 相关依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

#### 2.2 Redis配置

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    jedis:
      pool:
        # 连接池最大连接数,使用负值表示无限制。
        max-active: 8
        # 连接池最大阻塞等待时间,使用负值表示无限制。
        max-wait: -1s
        # 连接池最大空闲数,使用负值表示无限制。
        max-idle: 8
        # 连接池最小空闲连接，只有设置为正值时候才有效
        min-idle: 1
    timeout: 300ms
  session:
    # session 存储方式 支持redis、mongo、jdbc、hazelcast
    store-type: redis

# 如果是集群节点 采用如下配置指定节点
#spring.redis.cluster.nodes

```

有两点需要特别说明：

1. spring-session 不仅提供了redis作为公共session存储的方案，同时也支持jdbc、mongodb、Hazelcast等作为公共session的存储，可以用session.store-type 指定；
2. 对于redis 存储方案而言，官方也提供了不止一种整合方式，这里我们选取的整合方案是jedis客户端作为连接，当然也可以使用Lettuce作为客户端连接。

#### 2.3 启动类上添加@EnableRedisHttpSession 注解开启 spring-session-redis 整合方案的自动配置

```java
@SpringBootApplication
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 1800) //开启redis session支持,并配置session过期时间
public class SpringBootSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSessionApplication.class, args);
    }

}
```



## 三、验证分布式session

#### 3.1 创建测试controller和测试页面

```java
@Controller
public class LoginController {

    @RequestMapping
    public String index() {
        return "index";
    }

    @RequestMapping("home")
    public String home() {
        return "home";
    }

    @PostMapping("login")
    public String login(User user, HttpSession session) {
        // 随机生成用户id
        user.setUserId(Math.round(Math.floor(Math.random() * 10 * 1000)));
        // 将用户信息保存到id中
        session.setAttribute("USER", user);
        return "home";
    }

}
```

登录页面index.ftl：

```jsp
<!doctype html>
<html lang="en">
<head>
    <title>登录页面</title>
</head>
<body>
    <form action="/login" method="post">
        用户：<input type="text" name="username"><br/>
        密码：<input type="password" name="password"><br/>
        <button type="submit">登录</button>
    </form>
</body>
</html>
```

session 信息展示页面home.ftl：

```jsp
<!doctype html>
<html lang="en">
<head>
    <title>主页面</title>
</head>
<body>
    <h5>登录用户: ${Session["USER"].username} </h5>
    <h5>用户编号: ${Session["USER"].userId} </h5>
</body>
</html>
```

#### 3.2 启动项目

由于我们这里采用的是spring boot 的内置容器作为web容器，所以直接启动两个实例测试即可。

应用1启动配置：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-session-app1.png"/> </div>

应用2启动配置，需要用 `--server.port `指定不同的端口号：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-session-app2.png"/> </div>

**测试结果：**

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-session-8080.png"/> </div>

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-session-8090.png"/> </div>
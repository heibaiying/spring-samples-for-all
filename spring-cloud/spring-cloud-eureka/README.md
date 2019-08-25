# Eureka 服务的注册与发现


## 一、Eureka 简介

Spring Cloud Eureka 使用 Netﬂix Eureka 来实现服务注册与发现，它既包含了服务端组件，也包含了客户端组件：

- **Eureka 服务端**：服务的注册中心，负责维护注册的服务列表。
- **Eureka 客户端**： 在应用程序运行时，Eureka 客户端向注册中心注册自身提供的服务，并周期性地发送心跳来更新它的服务租约。同时它也能把从服务端查询到服务信息缓存到本地，并周期性地刷新服务状态。 




## 二、项目结构

- **eureka-server** 为服务注册中心，负责服务的管理；
- **eureka-client** 为 Eureka 客户端。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-eureka.png"/> </div>

## 三、三步搭建 Eureka 服务注册中心

### 3.1 服务端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### 3.2 服务端配置

```yaml
server:
  port: 8010
eureka:
  instance:
    hostname: localhost
  client:
    # 设置为 false,代表不向注册中心注册自己
    register-with-eureka: false
    # 注册中心主要用于维护服务，并不需要检索服务，所以设置为 false
    fetch-registry: false
    serviceUrl:
          defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

### 3.3 @EnableEurekaServer

在启动类上增加 @EnableEurekaServer 注解来激活 Eureka 服务端自动配置：

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

}
```



## 四、三步搭建 Eureka 客户端

### 4.1 客户端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 4.2 客户端配置

```yaml
server:
  port: 8020
# 指定服务命名
spring:
  application:
    name: eureka-client
# 指定注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8010/eureka/
```

### 4.3 @EnableDiscoveryClient

在启动类上增加 @EnableDiscoveryClient 注解来激活 Eureka 客户端自动配置：

```java
@SpringBootApplication
@EnableDiscoveryClient
public class EurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

}
```

## 五、启动项目 

进入注册中心控制台，查看服务注册情况：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka.png"/> </div>

# eureka 服务的注册与发现

## 一、项目结构

eureka-server为服务注册中心，负责服务的管理；

eureka-client 为eureka客户端；

![spring-cloud-eureka](D:\spring-samples-for-all\pictures\spring-cloud-eureka.png)

## 二、三步搭建eureka 服务注册中心

#### 2.1 引入eureka服务端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

#### 2.2 eureka 服务端配置

```yaml
server:
  port: 8010
eureka:
  instance:
    hostname: localhost
  client:
    # 设置为false,代表不向注册中心注册自己
    register-with-eureka: false
    # 注册中心主要用于维护服务，并不需要检索服务，所以设置为false
    fetch-registry: false
    serviceUrl:
          defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

#### 2.3 启动类上增加注解@EnableEurekaServer激活eureka服务端自动配置

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

}
```

## 三、三步搭建eureka 客户端

#### 3.1 引入eureka客户端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

#### 3.2 eureka 客户端配置

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

#### 3.3 启动类上增加注解@EnableDiscoveryClient激活eureka客户端自动配置

```java
@SpringBootApplication
@EnableDiscoveryClient
public class EurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

}
```

## 4.启动项目 

#### 4.1 进入注册中心控制台，查看服务注册情况

![eureka](D:\spring-samples-for-all\pictures\eureka.png)
# Eureka 高可用注册中心的搭建


<nav>
<a href="#一项目结构">一、项目结构</a><br/>
<a href="#二三步搭建-Eureka-高可用注册中心">二、三步搭建Eureka高可用注册中心</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-服务端依赖">2.1 服务端依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22--服务端配置">2.2  服务端配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-EnableEurekaServer">2.3 @EnableEurekaServer</a><br/>
<a href="#三三步搭建-Eureka-客户端">三、三步搭建Eureka客户端</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-客户端依赖">3.1 客户端依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-客户端配置">3.2 客户端配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#33-EnableDiscoveryClient">3.3 @EnableDiscoveryClient</a><br/>
<a href="#4启动项目">4.启动项目 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#41-启动注册中心">4.1 启动注册中心</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#42--集群搭建成功的判定">4.2  集群搭建成功的判定</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#43--prefer-ip-address-参数">4.3  prefer-ip-address 参数</a><br/>
</nav>

## 一、项目结构

- **eureka-server** 为服务注册中心，负责服务的管理；
- **eureka-client** 为 Eureka 客户端。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-eureka-cluster.png"/> </div>



## 二、三步搭建 Eureka 高可用注册中心

这里我们以单机伪集群的方式搭建，让三个单机注册中心互相注册，实现注册中心的高可用。配置示意图如下：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-server-client.png"/> </div>


### 2.1 服务端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### 2.2  服务端配置

创建三份配置文件，分别代表不同注册中心的配置：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-application.png"/> </div>


application-01.yml:

```yaml
spring:
  application:
    name: server
server:
  port: 8010
eureka:
  server:
    # 关闭自我保护机制 开发的时候可以开启 保证不可用的服务能够及时剔除
    enable-self-preservation: false
  instance:
    hostname: 127.0.0.1
  client:
    serviceUrl:
          defaultZone: http://localhost:8020/eureka/,http://192.168.200.228:8030/eureka/
```

application-02.yml

```yaml
spring:
  application:
    name: server
server:
  port: 8020
eureka:
  server:
    # 关闭自我保护机制 开发的时候可以开启 保证不可用的服务能够及时剔除
    enable-self-preservation: false
  instance:
    hostname: localhost
  client:
    serviceUrl:
          defaultZone: http://127.0.0.1:8010/eureka/,http://192.168.200.228:8030/eureka/
```

application-03.yml

```yaml
spring:
  application:
    name: server
server:
  port: 8030
eureka:
  server:
    # 关闭自我保护机制 开发的时候可以开启 保证不可用的服务能够及时从列表中剔除
    enable-self-preservation: false
  instance:
    hostname: 192.168.200.228
  client:
    serviceUrl:
          defaultZone: http://127.0.0.1:8010/eureka/,http://localhost:8020/eureka/
```

需要注意的是 Eureka 互相注册要求各个 Eureka 实例的 eureka.instance.hostname 不同，如果相同，则会被 Eureka 标记为 unavailable-replicas（不可用副本）。

### 2.3 @EnableEurekaServer

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



## 三、三步搭建 Eureka 客户端

### 3.1 客户端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 3.2 客户端配置

```yaml
server:
  port: 8040
# 指定服务命名
spring:
  application:
    name: eureka-client
# 指定注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:8010/eureka/,http://localhost:8020/eureka/,http://192.168.200.228:8030/eureka/
```

### 3.3 @EnableDiscoveryClient

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

## 4.启动项目 

### 4.1 启动注册中心

这里我们可以采用命令行方式指定配置，分别启动三个注册中心：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-active.png"/> </div>


### 4.2  集群搭建成功的判定

这里需要注意的是仅仅 Status 中出现其他注册中心时，并不一定是搭建成功的，**一定是当注册中心的 DS Replicas 和 available replicas 中显示其余的注册中心时候，才代表搭建成功**。

8010 注册中心：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-8010.png"/> </div>


8020 注册中心：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-8020.png"/> </div>


8030 注册中心：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-8030.png"/> </div>


Status 下的每个注册中心都可以点击跳转到其监控页面，但其监控页面地址链接可能是动态变化的，主要情况如下：

+ 当 hostname 和 prefer-ip-address 都没有配置，则访问 `主机名:服务名:端口号`：

```
    如：http://desktop-8jgsflj:8761/info
```

+ 当配置了 hostname 而没有配置 prefer-ip-address，则访问 `hostname:服务名:端口号`：

```
     如：http://server:8761/info
```
+ 如果配置了 prefer-ip-address，则访问 `ipAddress:服务名:端口号`：

```
     如：http://192.168.200.228:8761/info
```

### 4.3  prefer-ip-address 参数

在有的配置示例中，配置了 prefer-ip-address 为 true：

```properties
eureka.instance.prefer-ip-address=true
```

在多机器独立部署的情况下是没有问题的，配置 prefer-ip-address 为 ture，代表发现服务时候优先按照 IP 去搜寻，对于多集群而言，可以保证尽快准确搜索到服务。而对于单机部署来说，IP 地址都是相同的，这会导致其余注册中心出现在 unavailable-replicas (不可用副本) 中。所以单机部署时候不建议开启这个参数（默认值为 false），多机部署时候可以开启。

# eureka 高可用注册中心的搭建

## 一、项目结构

eureka-server为服务注册中心，负责服务的管理；

eureka-client 为eureka客户端；

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-eureka-cluster.png"/> </div>



## 二、三步搭建eureka 高可用注册中心

这里我们以单机伪集群的方式搭建，让三个单机注册中心互相注册，实现注册中心的高可用。配置示意图如下：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-server-client.png"/> </div>

#### 2.1 引入eureka服务端依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

#### 2.2  创建三份配置文件，分别代表不同注册中心的配置

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

需要注意的是Eureka互相注册要求各个Eureka实例的eureka.instance.hostname不同，如果相同，则会被Eureka标记为unavailable-replicas（不可用副本）。

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

#### 3.2 eureka 客户端配置,指定注册中心地址

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

### 4.1 这里我们可以采用命令行方式指定配置，分别启动三个注册中心

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-active.png"/> </div>

### 4.2  高可用集群搭建成功的判定

这里需要主要的是仅仅status中出现其他注册中心时，并不一定是搭建成功的，**一定是当注册中心的DS Replicas 和 available replicas中显示其余的注册中心时候**，才代表搭建成功。

#### **4.2.1  点击下面注册中心的可用实例列表中的地址，访问链接分以下几个情况：**

1. hostname和prefer-ip-address都没有配置，则访问 主机名:服务名:端口号，

```
 如：http://desktop-8jgsflj:8761/info
```

2. 配置了hostname而没有配置prefer-ip-address，则访问 hostname:服务名:端口号，

```
     如：http://server:8761/info
```
3. 如果配置了prefer-ip-address，则访问 ipAddress:服务名:端口号，
```
     如：http://192.168.200.228:8761/info
```
8010 注册中心：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-8010.png"/> </div>

8020 注册中心：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-8020.png"/> </div>

8030 注册中心：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/eureka-8030.png"/> </div>

### 4.3  prefer-ip-address 参数说明

在有的配置示例中，配置了prefer-ip-address为true。

```properties
eureka.instance.prefer-ip-address=true
```

在多机器独立部署的情况下是没有问题的，配置prefer-ip-address为ture，代表发现服务时候优先按照ip去搜寻，对于多集群而言，可以保证尽快准确搜索到服务。而对于单机部署来说，ip地址都是相同的，这会导致其余注册中心出现在unavailable-replicas(不可用副本)中。所以单机部署时候不建议开启这个参数（默认值为false），多机部署时候可以开启。

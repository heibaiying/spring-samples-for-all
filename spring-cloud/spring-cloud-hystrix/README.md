# spring-cloud-hystrix-turbine

## 一、hystrix 简介

#### 1.1 熔断器

在分布式系统中，由于服务之间相互的依赖调用，如果一个服务单元发生了故障就有可能导致故障蔓延至整个系统，从而衍生出一系列的保护机制，断路器就是其中之一。

断路器可以在服务单元发生故障的时候，及时切断与服务单元的连接，避免资源被长时间占用。spring cloud hystrix组件实现了断路器、线程隔离等一系列基本功能，并具有服务降级、服务熔断、请求缓存、请求合并以及服务监控等配套功能。



#### 1.2 hystrix 工作机制

- 当一个服务的处理请求的失败次数低于阈值时候，熔断器处于关闭状态，服务正常；
- 当一个服务的处理请求的失败次数大于阈值时候，熔断器开启，这时候所有的请求都会执行快速失败，是不会去调用实际的服务的；
- 当熔断器处于打开状态的一段时间后，熔断器处于半打开状态，这时候一定数量的请求回去调用实际的服务，如果调用成功，则代表服务可用了，熔断器关闭；如果还是失败，则代表服务还是不可用，熔断器继续关闭。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/circuitbreaker.png"/> </div>

## 二、项目结构

[spring-cloud-ribbon](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-ribbon)用例已经实现通过ribbon+restTemplate实现服务间的调用，本用例在其基础上进行hystrix 的整合。

+ common: 公共的接口和实体类；
+ consumer: 服务的消费者，采用RestTemplate调用产品服务；
+ producer：服务的提供者；
+ eureka: 注册中心；
+ turbine:多个熔断器的聚合监控。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-cloud-hystrix.png"/> </div>



## 三、整合 hystrix （以consumer模块为例）

#### 3.1 引入依赖

hystrix的仪表盘功能实际上是从[端点](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-actuator)获取数据，所以需要actuator starter开启端点的相关功能。

```xml
<!--hystrix 依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<!--hystrix 监控仪表盘依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
<!--健康检查依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### 3.2 暴露端点

```java
management:
  endpoints:
    web:
      exposure:
        # 需要开启hystrix.stream端点的暴露 这样才能获取到监控信息 * 代表开启所有可监控端点
        include: "*"
```

#### 3.3 在启动类上添加注解@EnableHystrix和@EnableHystrixDashboard

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
@EnableHystrixDashboard
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
```

#### 3.4  使用 @HystrixCommand 定义失败回退的方法

```java
@HystrixCommand(fallbackMethod = "queryProductsFail")
public List<Product> queryAllProducts() {
    ResponseEntity<List> responseEntity = restTemplate.getForEntity("http://producer/products", List.class);
    List<Product> productList = responseEntity.getBody();
    return productList;
}

// 如果发送熔断返回空集合，在前端判断处理
public List<Product> queryProductsFail() {
    return new ArrayList<>();
}
```

```html
<!doctype html>
<html lang="en">
<head>
    <title>产品列表</title>
</head>
<body>
<h3>产品列表:点击查看详情</h3>
<form action="/sell/product" method="post">
    <input type="text" name="productName">
    <input type="submit" value="新增产品">
</form>
<ul>
    <#if (products?size>0) >
        <#list products as product>
            <li>
                <a href="/sell/product/${product.id}">${product.name}</a>
            </li>
        </#list>
    <#else>
        <h4 style="color: red">当前排队人数过多，请之后再购买！</h4>
    </#if>
</ul>
</body>
</html>
```

#### 3.5 模拟熔断

这里被调用方采用线程休眠的方式模拟服务超时，Hystrix默认超时时间为2s,调用远程服务时候超过这个时间，会触发熔断。

```java
public List<Product> queryAllProducts() {
    // hystrix 默认超时是2秒
    int i = new Random().nextInt(2500);
    try {
        Thread.sleep(i);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return productList;
}
```

3.5   启动服务，访问http://localhost:8030/sell/products ，多次刷新查看熔断情况

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/hystrix-8030.png"/> </div>

#### 3.5 启动服务，访问 localhost:8030/hystrix

依次输出http://localhost:8030/actuator/hystrix.stream（监控地址） ，2000（延迟时间），title可以任意填写，进入监控台。

需要注意的是在spring cloud Finchley.SR2，监控地址中都是有/actuator的，因为在spring boot 2.x 的所有端点（包括自定义端点）都是暴露在这个路径下，在启动时候的控制台输出的日志可以查看到所有暴露端点的映射。

**登录页面**：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/hystrix-single-login.png"/> </div>

**监控页面**：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/hystrix-8030-login.png"/> </div>

**关于各个参数的说明参见[官方wiki](https://github.com/Netflix-Skunkworks/hystrix-dashboard/wiki)提供的图**：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/dashboard.png"/> </div>





## 四、使用turbine 实现聚合监控

单体监控和聚合监控：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/dashboard-direct-vs-turbine-640.png"/> </div>



#### 4.1 创建turbine模块，导入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.heibaiying.hystrix</groupId>
        <artifactId>spring-cloud-hystrx</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>turbine</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
           <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
        </dependency>
    </dependencies>


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



#### 4.2 指定注册中心地址和聚合的项目，这里我们监控 consumer,producer 两个项目

```java
server:
  port: 8040
# 指定服务命名
spring:
  application:
    name: turbine
# 指定注册中心地址
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8010/eureka/
# 指定聚合的项目
turbine:
  aggregator:
    cluster-config: default
  combine-host-port: true
  app-config: consumer,producer
  clusterNameExpression: "'default'"
```



#### 4.3 在启动类上添加注解

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
@EnableHystrixDashboard
@EnableTurbine
public class TurbineApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurbineApplication.class, args);
    }

}
```



#### 4.4 依次启动eureka、producer、consumer、turbine四个项目

在  localhost:8030/hystrix或者localhost:8030/hystrix（consumer和producer都集成了hystrix） 页面输入http://localhost:8040/turbine.stream，查看断路器聚合信息

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/hystrix-cluster-login.png"/> </div>

**显示了不同服务单元（consumer,producer）的多个断路器信息：**

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/hystrix-cluster.png"/> </div>

## 五、整合过程中可能出现的问题

#### 5.1 无法访问监控页面

1. 一般是端点链接输入不对，在F版本的spring cloud 中，输入监控的端点链接是 http://localhost:8030/actuator/hystrix.stream ，中间是有/actuator/（之前版本的没有/actuator/）

2.  没有暴露端点链接，暴露端点链接有两种方式，一种是我们在上文中提到的基于配置的方式

   ```yaml
   management:
     endpoints:
       web:
         exposure:
           # 需要开启hystrix.stream端点的暴露 这样才能获取到监控信息 * 代表开启所有可监控端点
           include: "*"
   ```

   第二种方式是基于代码的方式，如下：

   ```java
    @Bean
   public ServletRegistrationBean getServlet() {
       HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
       ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
       registrationBean.setLoadOnStartup(1);
       registrationBean.addUrlMappings("/actuator/hystrix.stream");
       registrationBean.setName("HystrixMetricsStreamServlet");
       return registrationBean;
   }
   ```

   这两种方式二选一即可，就算是采用代码的方式，还是建议将地址设置为/actuator/hystrix.stream，而不是原来的hystrix.stream，因为turbine默认也是从/actuator/hystrix.stream去获取信息。

#### 5.2 页面一直loading 或者访问端点页面一直出现ping

这种情况是熔断器所在的方法没有被调用，不是整合问题，这时候调用一下熔断器所在方法即可。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/hystrix-loading.png"/> </div>

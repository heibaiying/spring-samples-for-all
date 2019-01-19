# spring-sleuth-zipkin

## 一、简介

在微服务架构中，几乎每一个前端的请求都会经过多个服务单元协调来提供服务，形成复杂的服务调用链路。当服务发生问题时候，很难知道问题来源于链路的哪一个环节，这时候就需要进行链路追踪。

zipkin 是一个开源的分布式跟踪系统，可以使用spring cloud sleuth 来轻松的集成 zipkin。



## 二、项目结构

这里的项目是在之前的 [spring-cloud-zuul](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-zuul) 进行集成，zuul 项目的产品接口调用链路从 网关 -> consumer -> producer,历经三个环节的调用链路可以直观展示zipkin对链路追踪可视化的好处。

+ common: 公共的接口和实体类；
+ consumer: 服务的消费者，采用feign调用产品服务；
+ producer：服务的提供者；
+ eureka: 注册中心；
+ zuul: api网关。



## 三、构建 zipkin 服务端

zipkin 客户端可以不用自己构建，直接从[官网](https://zipkin.io/pages/quickstart)上下载对应的jar 包启动即可，默认端口 9411

```shell
java -jar zipkin.jar
```

可以直接从docker仓库拉取，然后启动容器：

```shell
docker run -d -p 9411:9411 openzipkin/zipkin
```

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/zipkin-download.png"/> </div>





## 四、集成zipkin

这里我们对zuul、consumer、producer 三个模块都进行集成

#### 4.1 对三个模块(zuul、consumer、producer )添加依赖

```xml
<!--zipkin-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

#### 4.2 分别在三个模块的application.yml 配置文件中指定zipkin的地址

```yaml
spring:
  zipkin:
    base-url: http://localhost:9411/
  # 可以指定监控数据的采样率
  sleuth:
    sampler:
      probability: 1
```



## 五、启动项目

分别启动，eureka，zuul，consumer，producer，zuul ，访问 http://localhost:9411/ ，查看我们的服务调用链路

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/zipkin.png"/> </div>

点击链路，则可以查看具体的调用情况

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/zipkin-detail.png"/> </div>

展示信息说明：

Span ： 基本工作单元，发送一个远程调度任务就会产生一个 Span。 

Trace：由一系列 Span 组成的，呈树状结构。 所有由这个请求产生的 Span 组成了这个 Trace 。 

SpanId ; 工作单元 (Span) 的唯一标识。 

TraceId :  一条请求链路 (Trace) 的唯 一 标识。

除了TraceID外，还需要SpanID用于记录调用父子关系。每个服务会记录下parent id和span id，通过他们可以组织一次完整调用链的父子关系。

注：关于以上概念可以比对链表的实现原理来理解。
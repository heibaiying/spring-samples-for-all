# Spring-Sleuth-Zipkin

<nav>
<a href="#一简介">一、简介</a><br/>
<a href="#二项目结构">二、项目结构</a><br/>
<a href="#三Zipkin-服务端">三、Zipkin 服务端</a><br/>
<a href="#四Zipkin-的集成">四、Zipkin 的集成 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#41-添加依赖">4.1 添加依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#42-连接到服务端">4.2 连接到服务端</a><br/>
<a href="#五启动项目">五、启动项目</a><br/>
</nav>

## 一、简介

在微服务架构中，几乎每一个前端的请求都会经过多个服务单元来协调提供服务，从而形成复杂的调用链路。此时如果服务发生问题，我们就很难知道其具体发生在哪一个环节，想要解决这个问题，可以使用链路追踪技术。Zipkin 是一个开源的分布式跟踪系统，Spring 支持使用 Spring Cloud Sleuth 来轻松地集成 Zipkin。



## 二、项目结构

这里的项目是在之前的 [spring-cloud-zuul](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-zuul) 上进行集成，该项目产品接口的调用链路为： 网关 -> consumer -> producer ，整个过程历经三个环节的调用，因此可以直观展示 Zipkin 链路追踪的效果。

+ **common**：公共的接口和实体类；
+ **consumer**：服务的消费者，采用 Feign 调用产品服务；
+ **producer**：服务的提供者；
+ **eureka**：注册中心；
+ **zuul**： API 网关。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-cloud-sleuth-zipkin.png"/> </div>


## 三、Zipkin 服务端

zipkin  服务端可以不用自己构建，直接从 [官网](https://zipkin.io/pages/quickstart) 上下载对应的 JAR 包即可，启动命令如下。默认端口为 9411：

```shell
java -jar zipkin.jar
```

也可以直接从 Docker 仓库拉取镜像，然后进行启动：

```shell
docker run -d -p 9411:9411 openzipkin/zipkin
```

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/zipkin-download.png"/> </div>






## 四、Zipkin 的集成 

这里我们对 zuul、consumer、producer 三个模块都进行集成：

### 4.1 添加依赖

对三个模块 (zuul、consumer、producer ) 添加 Zipkin 依赖：

```xml
<!--zipkin-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

### 4.2 连接到服务端

分别在三个模块的 application.yml 配置文件中指定 zipkin 的服务地址 ：

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

分别启动 eureka，zuul，consumer，producer 四个项目，访问 http://localhost:9411/ ，查看我们的服务调用链路：

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/zipkin.png"/> </div>


点击链路，即可以查看具体的调用情况：

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/zipkin-detail.png"/> </div>


展示信息说明：

- **Span**：基本工作单元，发送一个远程调度任务就会产生一个 Span。 
- **Trace**：由一系列 Span 组成的，呈树状结构。 所有由这个请求产生的 Span 组成了对应的 Trace 。 
- **SpanId**：工作单元 ( Span ) 的唯一标识。 
- **TraceId**：一条请求链路 ( Trace ) 的唯一标识。

除了 TraceID 外，还需要 SpanID 用于记录调用的父子关系。每个服务会记录下 parent id 和 span id，通过他们可以组成一条完整调用链，可以对比链表的实现原理来理解。

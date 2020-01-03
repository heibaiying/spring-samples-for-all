# Spring-Samples-For-All

![spring](https://img.shields.io/badge/spring-5.1.3.RELEASE-brightgreen.svg)    ![springboot](https://img.shields.io/badge/springboot-2.1.1.RELEASE-brightgreen.svg)    ![springcloud](https://img.shields.io/badge/springcloud-Finchley.SR2-brightgreen.svg)    ![jdk](https://img.shields.io/badge/jdk->=1.8-blue.svg)    ![author](https://img.shields.io/badge/author-heibaiying-orange.svg)

本项目仓库提供 spring、spring-boot、spring-cloud 的常用整合用例。**每个用例都提供详细的图文说明**，并给出官方文档的具体链接作为参考。随着 spring 的迭代，本仓库会持续更新，升级版本和丰富用例。

**版本说明**：

spring： 5.1.3.RELEASE

spring-boot：2.1.1.RELEASE

spring-cloud：Finchley.SR2
<br/>

>:star::star::star:**对大数据技术栈感兴趣的小伙伴可以关注我的新仓库：[大数据入门指南](https://github.com/heibaiying/BigData-Notes)**

## 1. spring samples

所有 spring 的项目我都会提供两个版本的 sample：

- 一个版本是基于 xml 配置，也就是最为常见的配置方式；
- 另一个版本完全基于代码配置（项目以**annotation**结尾），这也是目前 spring 官方推荐的更为灵活配置方法，也方便更好的衔接 spring boot 的配置。

| samples                                                      | 描述                                                         | 官方文档                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [springmvc-base](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/springmvc-base)<br/>[springmvc-base-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/springmvc-base-annotation) | springmvc 基础、参数绑定、格式转换、数据校验、<br/>异常处理、 文件上传下载、视图渲染 | [Spring Mvc ](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/web.html#mvc) |
| [spring-aop](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-aop)<br/>[spring-aop-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-aop-annotation) | spring 切面编程                                               | [Spring AOP](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/core.html#aop) |
| [spring-jdbc](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-jdbc)<br/>[spring-jdbc-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-jdbc-annotation) | spring jdbc-template 的使用                                  | [Using JdbcTemplate](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/data-access.html#jdbc-JdbcTemplate) |
| [spring-mybatis](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-mybatis)<br/>[spring-mybatis-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-mybatis-annotation) | spring 整合 mybatis                                          | [Mybatis-Spring](http://www.mybatis.org/spring/zh/index.html) |
| [spring-druid-mybatis](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-druid-mybatis)<br/>[spring-druid-mybatis-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-druid-mybatis-annotation) | spring 整合 druid、mybatis                                    | [Alibaba druid](https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98) |
| [spring-redis](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-redis)<br/>[spring-redis-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-redis-annotation) | spring 整合 redis 单机 + 集群（jedis 客户端）<br/>spring 整合 redis 单机 + 集群（redisson 客户端） | [Redisson](https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95) |
| [spring-mongodb](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-mongodb)<br/>[spring-mongodb-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-mongodb-annotation) | spring 整合 mongodb                                          | [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/docs/2.1.3.RELEASE/reference/html/#mongo.mongo-db-factory-java) |
| [spring-memcached](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-memcached)<br/>[spring-memcached-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-memcached-annotation) | spring 整合 memcached(单机 + 集群)                             | [Xmemcached](https://github.com/killme2008/xmemcached/wiki/Xmemcached%20%E4%B8%AD%E6%96%87%E7%94%A8%E6%88%B7%E6%8C%87%E5%8D%97) |
| [spring-rabbitmq](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-rabbitmq)<br/>[spring-rabbitmq-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-rabbitmq-annotation) | spring 整合 rabbitmq、消息序列化与反序列化                   | [Rabbitmq](http://www.rabbitmq.com/getstarted.html)<br>[Spring AMQP](https://docs.spring.io/spring-amqp/docs/2.1.3.BUILD-SNAPSHOT/reference/html/) |
| [spring-dubbo](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-dubbo)<br/>[spring-dubbo-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-dubbo-annotation) | spring 整合 dubbo                                            | [Dubbo ](http://dubbo.apache.org/zh-cn/docs/user/quick-start.html) |
| [spring-websocket](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-websocket)<br/>[spring-websocket-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-websocket-annotation) | spring 整合 websocket                                        | [Spring Websocket](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/web.html#websocket) |
| [spring-mail](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-email) <br/>[spring-mail-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-email-annotation) | spring 普通文本邮件、附件邮件、模板邮件                      | [Spring Email](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/integration.html#mail) |
| [spring-scheduling](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-scheduling)<br/>[spring-scheduling-annotation](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring/spring-scheduling-annotation) | spring 定时任务                                              | [Task Execution and Scheduling](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/integration.html#scheduling) |

<br/>

## 2. spring-boot samples

| samples                                                      | 描述                                       | 官方文档                                                     |
| ------------------------------------------------------------ | ------------------------------------------ | ------------------------------------------------------------ |
| [spring-boot-base](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-base) | spring-boot 基础                           | [spring boot 官方文档](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/)<br>[spring boot 中文官方文档](https://www.breakyizhan.com/springboot/3028.html) |
| [spring-boot-yml-profile](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-yml-profile) | yml 语法和多配置切换                       | [Using YAML Instead of Properties](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#boot-features-external-config-yaml) |
| [spring-boot-tomcat](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-tomcat) | spring-boot 整合外部容器（tomcat）         | [Use Another Web Server](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#howto-use-another-web-server) |
| [spring-boot-servlet](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-servlet) | spring boot 整合 servlet 3.0                | [Embedded Servlet Container Support](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#boot-features-embedded-container) |
| [spring-boot-jsp](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-jsp) | spring-boot 整合 jsp（内置容器）           | [JSP Limitations](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#boot-features-jsp-limitations) |
| [spring-boot-data-jpa](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-data-jpa) | spring-boot data jpa 的使用                | [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/2.1.3.RELEASE/reference/html/) |
| [spring-boot-mybatis](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-mybatis) | spring-boot+HikariDataSources 整合 mybatis | [Mybatis-Spring](http://www.mybatis.org/spring/zh/index.html)<br/>[Mybatis-Spring-Boot-Autoconfigure](http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/) |
| [spring-boot-druid-mybtais](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-druid-mybatis) | spring-boot 整合 druid、mybatis             | [Alibaba druid](https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)<br/>[druid-spring-boot-starter](https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter) |
| [spring-boot-redis](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-redis) | spring-boot 整合 redis                     | [Working with NoSQL Technologies](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#boot-features-nosql) |
| [spring-boot-mongodb](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-mongodb) | spring-boot 整合 mongodb                   | [Working with NoSQL Technologies](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#boot-features-nosql) |
| [spring-boot-memcached](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-memcached) | spring-boot 整合 memcached                 | [Xmemcached](https://github.com/killme2008/xmemcached/wiki/Xmemcached%20%E4%B8%AD%E6%96%87%E7%94%A8%E6%88%B7%E6%8C%87%E5%8D%97) |
| [spring-boot-rabbitmq](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-rabbitmq) | spring-boot 整合 rabbitmq                  | [RabbitMQ support](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#boot-features-rabbitmq) |
| [spring-boot-dubbo](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-dubbo) | spring-boot 整合 dubbo                     | [Dubbo ](http://dubbo.apache.org/zh-cn/docs/user/quick-start.html) |
| [spring-boot-websocket](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-websocket) | spring-boot 整合 websocket                 | [Using @ServerEndpoint](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#howto-create-websocket-endpoints-using-serverendpoint) |
| [spring-boot-kafka](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-kafka) | spring-boot 整合 kafka                     | [Apache Kafka Support](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#boot-features-kafka) |
| [spring-boot-actuator](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-actuator) | actuator + Hyperic SIGAR 应用信息监控      | [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/#production-ready) |
| [spring-boot-swagger2](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-swagger2) | spring-boot 集成 Swagger2 打造在线接口文档 | [Springfox Reference Documentation](http://springfox.github.io/springfox/docs/current/) |

<br/>

## 3. spring-cloud  samples

| samples                                                      | 描述                                                         | 官方文档                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [spring-cloud-Eureka](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-eureka) | Eureka 服务的注册和发现                                      | [Service Discovery: Eureka Server](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi_spring-cloud-eureka-server.html) |
| [spring-cloud-Eureka-cluster](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-eureka-cluster) | Eureka 高可用集群搭建                                        | [Service Discovery: Eureka Server](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi_spring-cloud-eureka-server.html) |
| [spring-cloud-Ribbon](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-ribbon) | Ribbon 客户端负载均衡<br/>RestTemplate 服务远程调用          | [Client Side Load Balancer: Ribbon](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi_spring-cloud-ribbon.html) |
| [spring-cloud-OpenFeign](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-feign) | OpenFeign 声明式服务调用、服务容错处理                       | [Declarative REST Client: Feign](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi_spring-cloud-feign.html) |
| [spring-cloud-Hystrix](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-hystrix) | Hystix 服务容错保护<br/>hystrix dashboard 断路器监控<br>Turbine 断路器聚合监控 | [Circuit Breaker: Hystrix Clients](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi__circuit_breaker_hystrix_clients.html)<br/>[Hystrix metrics aggregation with Turbine](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi_spring-cloud-consul-turbine.html) |
| [spring-cloud-Zuul](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-zuul) | Zuul 网关服务                                                | [Router and Filter: Zuul](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi__router_and_filter_zuul.html) |
| [spring-cloud-Sleuth-Zipkin](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-sleuth-zipkin) | Sleuth + Zipkin 服务链路追踪                                 | [Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi__introduction.html#sleuth-adding-project) |
| [spring-cloud-Config-Bus](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-cloud/spring-cloud-config) | Config 分布式配置中心 <br>集成 Bus 消息总线 实现配置热更新     | [Spring Cloud Config Client](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/multi/multi__spring_cloud_config_client.html) |

<br/>

## 4.spring分布式session和分布式事务

| sample                                                       | 描述                                                         | 官方文档                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [spring-session](https://github.com/heibaiying/spring-samples-for-all/tree/master/distributed-solution/spring-session) | spring 实现分布式 session                                    | [spring session](https://spring.io/projects/spring-session#learn) |
| [spring boot + spring session](https://github.com/heibaiying/spring-samples-for-all/tree/master/distributed-solution/spring-boot-session) | spring boot + spring session 实现分布式 session              | [spring session](https://spring.io/projects/spring-session#learn) |
| [springboot-druid-mybatis-atomikos](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/springboot-druid-mybatis-multi) | spring boot + druid + mybatis + atomikos<BR> 配置多数据源、支持分布式事务 ( JTA 方式实现) | [Distributed Transactions with JTA](https://docs.spring.io/spring-boot/docs/2.1.2.RELEASE/reference/htmlsingle/#boot-features-jta) |

<br/>

## 5.参考资料

相关参考文档放在了仓库的 referenced documents 目录下，文件目录如下：

- Servlet3.1 规范（最终版）.pdf
- Thymeleaf 中⽂参考⼿册.pdf

<br>

<div align="center"> <img width="200px" src="pictures/blog-logo.png"/> </div>

<div align="center"> <a  href = "https://blog.csdn.net/m0_37809146"> 欢迎关注我的博客：https://blog.csdn.net/m0_37809146</a> </div>

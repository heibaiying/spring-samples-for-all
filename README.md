# Spring-All-In-One

该仓库提供spring、spring-boot、spirng-cloud 的常用samples。每个用例都提供详细的注释和搭建说明（详见每个sample项目根目录README.md），旨在总结自己工作和学习的结果，也为广大的Java学习和爱好者提供参考。另外因为samples 并不能涵盖该知识点的所有内容，所以每个sample我都会附上本部分内容官方文档的链接作为参考。

**版本说明**：

spring： 5.1.3.RELEASE

spring-boot：2.1.1.RELEASE

spring-cloud：Finchley.SR2

该仓库建立于2018年12月，所采用的都是spring各个组件最新的版本，之后也会随着版本的更新更新仓库代码。

**更新进度说明：**

下方表格中首列为项目地址链接，所有samples预计在1月底前完成。

<br/>

## 1. spring samples

注1：所有spring的项目我都会提供两个版本的sample,

- 一个版本是基于xml配置，也就是大家最为常见的配置方式。
- 另一个版本完全基于代码配置，不含任何的xml文件,这也是spring 4.0 之后官方推荐的更为灵活的配置方法，也方便更好的衔接spring boot 的配置。（项目以**annotation**结尾）

| samples                                                      | 描述                                                         | 官方文档                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| [springmvc-base](https://github.com/heibaiying/Spring-All-In-One/tree/master/spring/springmvc-base)<br/>[springmvc-base-annotation](https://github.com/heibaiying/Spring-All-In-One/tree/master/spring/springmvc-base-annotation) | springmvc基础、参数绑定、参数转换、数据校验、<br/>全局异常处理、 文件上传下载、jsp视图 | [spring mvc ](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/web.html#mvc) |
| [spring-jdbc](https://github.com/heibaiying/Spring-All-In-One/tree/master/spring/spring-jdbc)<br/>[spring-jdbc-annotation](https://github.com/heibaiying/Spring-All-In-One/tree/master/spring/spring-jdbc-annotation) | spring jdbc-template 的使用                                  | [Using JdbcTemplat](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/data-access.html#jdbc-JdbcTemplate) |
| [spring-mybatis](https://github.com/heibaiying/Spring-All-In-One/tree/master/spring/spring-mybatis)<br/>[spring-mybatis-annotation](https://github.com/heibaiying/Spring-All-In-One/tree/master/spring/mybatis-annotation) | spring 整合 mybatis                                          | [mybatis-spring中文文档](http://www.mybatis.org/spring/zh/index.html) |
| spring-druid-mybtais<br/>spring-druid-mybtais-annotation     | spring 整合druid、mybatis                                    |                                                              |
| spring-druid-redis<br/>spring-druid-redis-annotation         | spring 整合 redis                                            |                                                              |
| spring-druid-mongodb<br/>spring-druid-mongodb-annotation     | spring 整合 mongodb                                          |                                                              |
| spring-druid-memcached<br/>spring-druid-memcached-annotation | spring 整合 memcached                                        |                                                              |
| spring-druid-rabbitmq<br/>spring-druid-rabbitmq-annotation   | spring 整合 rabbitmq                                         |                                                              |
| spring-druid-kafka<br/>spring-druid-kafka-annotation         | spring 整合 kafka                                            |                                                              |
| spring-druid-dubbo<br/>spring-druid-dubbo-annotation         | spring 整合 dubbo                                            |                                                              |
| spring-druid-websocket<br/>spring-druid-websocket-annotation | spring 整合 websocket                                        |                                                              |
| spring-druid-netty<br/>spring-druid-netty-annotation         | spring 整合 netty                                            |                                                              |
| spring-druid-scheduled<br/>spring-druid-scheduled-annotation | spring 定时任务                                              |                                                              |

<br/>

## 2. spring-boot samples

| samples                     | 描述                           | 官方文档                                                     |
| --------------------------- | ------------------------------ | ------------------------------------------------------------ |
| spring-boot-base            | spring-boot 基础               | [spring boot 官方文档](https://docs.spring.io/spring-boot/docs/2.1.1.RELEASE/reference/htmlsingle/) |
| spring-boot-aop             | spring aop                     |                                                              |
| spring-boot-cache           | spring-boot 缓存               |                                                              |
| spring-boot-profile         | spring 场景切换                |                                                              |
| spring-boot-servlet         | 整合servlet 3.0                |                                                              |
| spring-boot-test            | spring-boot 单元测试           |                                                              |
| spring-boot-jpa             | spring-boot jpa 的使用         |                                                              |
| spring-boot-freemarker      | freemarker 的使用              |                                                              |
| spring-boot-jsp             | spring-boot 整合 jsp           |                                                              |
| spring-boot-mybatis         | spring-boot 整合 mybatis       |                                                              |
| spring-boot-druid-mybtais   | spring-boot 整合druid、mybatis |                                                              |
| spring-boot-druid-redis     | spring-boot 整合 redis         |                                                              |
| spring-boot-druid-mongodb   | spring-boot 整合 mongodb       |                                                              |
| spring-boot-druid-memcached | spring-boot 整合 memcached     |                                                              |
| spring-boot-druid-rabbitmq  | spring-boot 整合 rabbitmq      |                                                              |
| spring-boot-druid-kafka     | spring-boot 整合 kafka         |                                                              |
| spring-boot-druid-dubbo     | spring-boot 整合 dubbo         |                                                              |
| spring-boot-druid-websocket | spring-boot 整合 websocket     |                                                              |
| spring-boot-druid-netty     | spring-boot 整合 netty         |                                                              |
| spring-boot-druid-scheduled | spring-boot 定时任务           |                                                              |

更多的场景和用例可参阅 [spring-boot 官方samples ](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples)

<br/>

## 3. spring-cloud samples

| samples                    | 描述                              | 官方文档                                                     |
| -------------------------- | --------------------------------- | ------------------------------------------------------------ |
| spring-cloud-Eureka        | spring cloud 服务的注册和发现     |                                                              |
| spring-cloud-Feign         | spring cloud 服务间通信           |                                                              |
| spring-cloud-config        | spring cloud 统一配置中心         |                                                              |
| spring-cloud-stream        | spring cloud 对消息服务的抽象整合 | [spring-cloud-stream官方文档](https://cloud.spring.io/spring-cloud-stream/) |
| spring-cloud-zuul          | spring cloud 网关限流、权限验证   |                                                              |
| spring-cloud-sleuth-Zipkin | spring cloud 服务追踪             |                                                              |

推荐课程：[Spring Cloud微服务实战](https://coding.imooc.com/class/187.html) 廖大神结合实战，很详细的介绍spring cloud各个组件的使用。

<br/>

## 4.参考资料

代码涉及到的相关参考资料放在了仓库的referenced documents 目录下，文件清单如下：

- Servlet3.1规范（最终版）.pdf


# spring boot 整合 rabbitmq
## 目录<br/>
<a href="#一-项目结构说明">一、 项目结构说明</a><br/>
<a href="#二关键依赖">二、关键依赖</a><br/>
<a href="#三公共模块rabbitmq-common">三、公共模块（rabbitmq-common）</a><br/>
<a href="#四服务消费者rabbitmq-consumer">四、服务消费者（rabbitmq-consumer）</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#41-消息消费者配置">4.1 消息消费者配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#42-使用注解@RabbitListener和@RabbitHandler创建消息监听者">4.2 使用注解@RabbitListener和@RabbitHandler创建消息监听者</a><br/>
<a href="#五-消息生产者rabbitmq-producer">五、 消息生产者（rabbitmq-producer）</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#51-消息生产者配置">5.1 消息生产者配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#52--创建消息生产者">5.2  创建消息生产者</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#53--以单元测试的方式发送消息">5.3  以单元测试的方式发送消息</a><br/>
<a href="#六项目构建的说明">六、项目构建的说明</a><br/>
## 正文<br/>


## 一、 项目结构说明

1.1  之前关于spring 整合 rabbitmq 我们采用的是单项目的方式，为了使得用例更具有实际意义，这里采用maven多模块的构建方式，在spring-boot-rabbitmq下构建三个子模块：

1. rabbitmq-common 是公共模块，用于存放公共的接口、配置和bean,被rabbitmq-producer和rabbitmq-consumer在pom.xml中引用；
2. rabbitmq-producer 是消息的生产者模块；
3. rabbitmq-consumer是消息的消费者模块。

1.2  关于rabbitmq安装、交换机、队列、死信队列等基本概念可以参考我的手记[《RabbitMQ实战指南》读书笔记](https://github.com/heibaiying/LearningNotes/blob/master/notes/%E4%B8%AD%E9%97%B4%E4%BB%B6/RabbitMQ/%E3%80%8ARabbitMQ%E5%AE%9E%E6%88%98%E6%8C%87%E5%8D%97%E3%80%8B%E8%AF%BB%E4%B9%A6%E7%AC%94%E8%AE%B0.md),里面有详细的配图说明。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-rabbitmq.png"/> </div>



## 二、关键依赖

在父工程的项目中统一导入依赖rabbitmq的starter(spring-boot-starter-amqp)，父工程的pom.xml如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <packaging>pom</packaging>

    <modules>
        <module>rabbitmq-consumer</module>
        <module>rabbitmq-producer</module>
        <module>rabbitmq-common</module>
    </modules>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.heibaiying</groupId>
    <artifactId>spring-boot-rabbitmq</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-boot-rabbitmq</name>
    <description>RabbitMQ project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
</project>
```



## 三、公共模块（rabbitmq-common）

- bean 下为公共的实体类。
- constant 下为公共配置，用静态常量引用。（这里我使用静态常量是为了方便引用，实际中也可以按照情况，抽取为公共配置文件）

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/rabbitmq-common.png"/> </div>

```java
package com.heibaiying.constant;

/**
 * @author : heibaiying
 * @description : rabbit 公用配置信息
 */
public class RabbitInfo {

    // queue 配置
    public static final String QUEUE_NAME = "spring.boot.simple.queue";
    public static final String QUEUE_DURABLE = "true";

    // exchange 配置
    public static final String EXCHANGE_NAME = "spring.boot.simple.exchange";
    public static final String EXCHANGE_TYPE = "topic";

    // routing key
    public static final String ROUTING_KEY = "springboot.simple.*";
}

```



## 四、服务消费者（rabbitmq-consumer）

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/rabbitmq-consumer.png"/> </div>

#### 4.1 消息消费者配置

```yaml
spring:
  rabbitmq:
    addresses: 127.0.0.1:5672
    # RabbitMQ 默认的用户名和密码都是guest 而虚拟主机名称是 "/"
    # 如果配置其他虚拟主机地址，需要预先用管控台或者图形界面创建 图形界面地址 http://主机地址:15672
    username: guest
    password: guest
    virtual-host: /
    listener:
      simple:
        # 为了保证信息能够被正确消费,建议签收模式设置为手工签收,并在代码中实现手工签收
        acknowledge-mode: manual
        # 侦听器调用者线程的最小数量
        concurrency: 10
        # 侦听器调用者线程的最大数量
        max-concurrency: 50
```

#### 4.2 使用注解@RabbitListener和@RabbitHandler创建消息监听者

1. 使用注解创建的交换机、队列、和绑定关系会在项目初始化的时候自动创建，但是不会重复创建；
2. 这里我们创建两个消息监听器，分别演示消息是基本类型和消息是对象时的配置区别。

```java
/**
 * @author : heibaiying
 * @description : 消息是对象的消费者
 */

@Component
@Slf4j
public class RabbitmqBeanConsumer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitBeanInfo.QUEUE_NAME, durable = RabbitBeanInfo.QUEUE_DURABLE),
            exchange = @Exchange(value = RabbitBeanInfo.EXCHANGE_NAME, type = RabbitBeanInfo.EXCHANGE_TYPE),
            key = RabbitBeanInfo.ROUTING_KEY)
    )
    @RabbitHandler
    public void onMessage(@Payload Programmer programmer, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        log.info("programmer:{} ", programmer);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
    }
}
```

```java
@Component
@Slf4j
public class RabbitmqConsumer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitInfo.QUEUE_NAME, durable = RabbitInfo.QUEUE_DURABLE),
            exchange = @Exchange(value = RabbitInfo.EXCHANGE_NAME, type = RabbitInfo.EXCHANGE_TYPE),
            key = RabbitInfo.ROUTING_KEY)
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageHeaders headers = message.getHeaders();
        // 获取消息头信息和消息体
        log.info("msgInfo:{} ; payload:{} ", headers.get("msgInfo"), message.getPayload());
        //  DELIVERY_TAG 代表 RabbitMQ 向该Channel投递的这条消息的唯一标识ID，是一个单调递增的正整数
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        // 第二个参数代表是否一次签收多条,当该参数为 true 时，则可以一次性确认 DELIVERY_TAG 小于等于传入值的所有消息
        channel.basicAck(deliveryTag, false);
    }

}
```



## 五、 消息生产者（rabbitmq-producer）

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/rabbitmq-producer.png"/> </div>

#### 5.1 消息生产者配置

```yaml
spring:
  rabbitmq:
    addresses: 127.0.0.1:5672
    # RabbitMQ 默认的用户名和密码都是guest 而虚拟主机名称是 "/"
    # 如果配置其他虚拟主机地址，需要预先用管控台或者图形界面创建 图形界面地址 http://主机地址:15672
    username: guest
    password: guest
    virtual-host: /
    # 是否启用发布者确认 具体确认回调实现见代码
    publisher-confirms: true
    # 是否启用发布者返回 具体返回回调实现见代码
    publisher-returns: true
    # 是否启用强制消息 保证消息的有效监听
    template.mandatory: true

server:
  port: 8090
```

#### 5.2  创建消息生产者

```java
/**
 * @author : heibaiying
 * @description : 消息生产者
 */
@Component
@Slf4j
public class RabbitmqProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSimpleMessage(Map<String, Object> headers, Object message,
                                  String messageId, String exchangeName, String key) {
        // 自定义消息头
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        // 创建消息
        Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);
        /* 确认的回调 确认消息是否到达 Broker 服务器 其实就是是否到达交换器
           如果发送时候指定的交换器不存在 ack就是false 代表消息不可达 */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("correlationData：{} , ack:{}", correlationData.getId(), ack);
            if (!ack) {
                System.out.println("进行对应的消息补偿机制");
            }
        });
        /* 消息失败的回调
         * 例如消息已经到达交换器上，但路由键匹配任何绑定到该交换器的队列，会触发这个回调，此时 replyText: NO_ROUTE
         */
        rabbitTemplate.setReturnCallback((message1, replyCode, replyText, exchange, routingKey) -> {
            log.info("message:{}; replyCode: {}; replyText: {} ; exchange:{} ; routingKey:{}",
                    message1, replyCode, replyText, exchange, routingKey);
        });
        // 在实际中ID 应该是全局唯一 能够唯一标识消息 消息不可达的时候触发ConfirmCallback回调方法时可以获取该值，进行对应的错误处理
        CorrelationData correlationData = new CorrelationData(messageId);
        rabbitTemplate.convertAndSend(exchangeName, key, msg, correlationData);
    }
}
```

#### 5.3  以单元测试的方式发送消息

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqProducerTests {

    @Autowired
    private RabbitmqProducer producer;

    /***
     * 发送消息体为简单数据类型的消息
     */
    @Test
    public void send() {
        Map<String, Object> heads = new HashMap<>();
        heads.put("msgInfo", "自定义消息头信息");
        // 模拟生成消息ID,在实际中应该是全局唯一的 消息不可达时候可以在setConfirmCallback回调中取得，可以进行对应的重发或错误处理
        String id = String.valueOf(Math.round(Math.random() * 10000));
        producer.sendSimpleMessage(heads, "hello Spring", id, RabbitInfo.EXCHANGE_NAME, "springboot.simple.abc");
    }


    /***
     * 发送消息体为bean的消息
     */
    @Test
    public void sendBean() {
        String id = String.valueOf(Math.round(Math.random() * 10000));
        Programmer programmer = new Programmer("xiaoMing", 12, 12123.45f, new Date());
        producer.sendSimpleMessage(null, programmer, id, RabbitBeanInfo.EXCHANGE_NAME, RabbitBeanInfo.ROUTING_KEY);
    }

}
```



## 六、项目构建的说明

因为在项目中，consumer和producer模块均依赖公共模块,所以在构建consumer和producer项目前需要将common 模块安装到本地仓库，**依次**对**父工程**和**common模块**执行：

```shell
mvn install -Dmaven.test.skip = true
```

consumer中 pom.xml如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.heibaiying</groupId>
        <artifactId>spring-boot-rabbitmq</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>rabbitmq-consumer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>rabbitmq-consumer</name>
    <description>RabbitMQ consumer project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.heibaiying</groupId>
            <artifactId>rabbitmq-common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <scope>compile</scope>
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

producer中 pom.xml如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.heibaiying</groupId>
        <artifactId>spring-boot-rabbitmq</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>rabbitmq-producer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>rabbitmq-producer</name>
    <description>RabbitMQ producer project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.heibaiying</groupId>
            <artifactId>rabbitmq-common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <scope>compile</scope>
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

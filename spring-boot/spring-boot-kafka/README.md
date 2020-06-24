# Spring Boot 整合 Kafka

<nav>
<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-主要依赖">1.2 主要依赖</a><br/>
<a href="#二-整合-Kafka">二、 整合 Kafka</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-基本配置">2.1 基本配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-消息发送">2.2 消息发送</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23--消息监听">2.3  消息监听</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-整合测试">2.4 整合测试</a><br/>
<a href="#三多消费者组测试">三、多消费者组测试</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31--创建多分区主题">3.1  创建多分区主题</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-消息监听">3.2 消息监听</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#33-消息发送">3.3 消息发送</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#34-测试结果">3.4 测试结果</a><br/>
<a href="#四序列化与反序列化">四、序列化与反序列化</a><br/>
</nav>

## 一、项目说明

### 1.1 项目结构

 本项目提供 Kafka 发送简单消息、对象消息、和多消费者组消费消息三种情况下的 sample：

- **kafkaSimpleConsumer** ：用于普通消息的监听；
- **kafkaBeanConsumer** ：用于对象消息的监听；
- **kafkaGroupConsumer** ：用于多消费者组和多消费者对主题分区消息监听的情况。



<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-boot-kafka.png"/> </div>

### 1.2 主要依赖

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
```



## 二、 整合 Kafka

### 2.1 基本配置

```yaml
spring:
  kafka:
    # 以逗号分隔的地址列表，用于建立与 Kafka 集群的初始连接 (kafka 默认的端口号为 9092)
    bootstrap-servers: 127.0.0.1:9092
    producer:
      # 发生错误后，消息重发的次数。
      retries: 0
      #当有多个消息需要被发送到同一个分区时，生产者会把它们放在同一个批次里。该参数指定了一个批次可以使用的内存大小，按照字节数计算。
      batch-size: 16384
      # 设置生产者内存缓冲区的大小。
      buffer-memory: 33554432
      # 键的序列化方式
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      # 值的序列化方式
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      # acks=0 ： 生产者在成功写入消息之前不会等待任何来自服务器的响应。
      # acks=1 ： 只要集群的首领节点收到消息，生产者就会收到一个来自服务器成功响应。
      # acks=all ：只有当所有参与复制的节点全部收到消息时，生产者才会收到一个来自服务器的成功响应。
      acks: 1
    consumer:
      # 自动提交的时间间隔 在 spring boot 2.X 版本中这里采用的是值的类型为 Duration 需要符合特定的格式，如 1S,1M,2H,5D
      auto-commit-interval: 1S
      # 该属性指定了消费者在读取一个没有偏移量的分区或者偏移量无效的情况下该作何处理：
      # latest（默认值）在偏移量无效的情况下，消费者将从最新的记录开始读取数据（在消费者启动之后生成的记录）
      # earliest ：在偏移量无效的情况下，消费者将从起始位置读取分区的记录
      auto-offset-reset: earliest
      # 是否自动提交偏移量，默认值是 true,为了避免出现重复数据和数据丢失，可以把它设置为 false,然后手动提交偏移量
      enable-auto-commit: true
      # 键的反序列化方式
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      # 值的反序列化方式
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    listener:
      # 在侦听器容器中运行的线程数。
      concurrency: 5

```

在 Spring Boot 2.x 后 auto-commit-interval（自动提交的时间间隔）采用的是值的类型为 Duration ，Duration 是 JDK 1.8 后引入的类，在其源码中我们可以看到对于其字符串的表达需要符合一定的规范，即数字 + 单位，如下的写法 1s ，1.5s， 0s， 0.001S ，1h， 2d 都是有效的。如果传入无效的字符串，则 Spring Boot 在启动阶段解析配置文件时就会抛出异常。 

```java
public final class Duration
        implements TemporalAmount, Comparable<Duration>, Serializable {

    /**
     * The pattern for parsing.
     */
    private static final Pattern PATTERN =
            Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?" +
                    "(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", Pattern.CASE_INSENSITIVE);
   
    ........                 
 
}
```

### 2.2 消息发送

使用 KafkaTemplate 来发送消息：

```java
@Component
@Slf4j
public class KafKaCustomrProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, Object object) {

        /*
         * 这里的 ListenableFuture 类是 spring 对 java 原生 Future 的扩展增强,是一个泛型接口,用于监听异步方法的回调
         * 而对于 kafka send 方法返回值而言，这里的泛型所代表的实际类型就是 SendResult<K, V>,而这里 K,V 的泛型实际上
         * 被用于 ProducerRecord<K, V> producerRecord,即生产者发送消息的 key,value 类型
         */
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, object);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.info("发送消息失败:" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> sendResult) {
                System.out.println("发送结果:" + sendResult.toString());
            }
        });
    }
}

```

### 2.3  消息监听

使用 @KafkaListener 注解来实现消息的监听：

```java
@Component
@Slf4j
public class KafkaSimpleConsumer {

    // 简单消费者
    @KafkaListener(groupId = "simpleGroup", topics = Topic.SIMPLE)
    public void consumer1_1(ConsumerRecord<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, Consumer consumer) {
        System.out.println("消费者收到消息:" + record.value() + "; topic:" + topic);
        /*
         * 如果需要手工提交异步 consumer.commitSync();
         * 手工同步提交 consumer.commitAsync()
         */
    }
}
```

### 2.4 整合测试

```java
@Slf4j
@RestController
public class SendMsgController {

    @Autowired
    private KafKaCustomrProducer producer;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /***
     * 发送消息体为基本类型的消息
     */
    @GetMapping("sendSimple")
    public void sendSimple() {
        producer.sendMessage(Topic.SIMPLE, "hello spring boot kafka");
    }
}
```



## 三、多消费者组测试

### 3.1  创建多分区主题

```java
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic groupTopic() {
        // 指定主题名称，分区数量，和复制因子
        return new NewTopic(Topic.GROUP, 10, (short) 2);
    }
}
```

### 3.2 消息监听

创建多消费者，并监听同一主题的不同分区：

- 消费者 1-1 监听主题的 0、1 分区
- 消费者 1-2 监听主题的 2、3 分区
- 消费者 1-3 监听主题的 0、1 分区
- 消费者 2-1 监听主题的所有分区

```java
@Component
@Slf4j
public class KafkaGroupConsumer {

    // 分组 1 中的消费者 1
    @KafkaListener(id = "consumer1-1", groupId = "group1", topicPartitions =
            {@TopicPartition(topic = Topic.GROUP, partitions = {"0", "1"})
            })
    public void consumer1_1(ConsumerRecord<String, Object> record) {
        System.out.println("consumer1-1 收到消息:" + record.value());
    }

    // 分组 1 中的消费者 2
    @KafkaListener(id = "consumer1-2", groupId = "group1", topicPartitions =
            {@TopicPartition(topic = Topic.GROUP, partitions = {"2", "3"})
            })
    public void consumer1_2(ConsumerRecord<String, Object> record) {
        System.out.println("consumer1-2 收到消息:" + record.value());
    }

    // 分组 1 中的消费者 3
    @KafkaListener(id = "consumer1-3", groupId = "group1", topicPartitions =
            {@TopicPartition(topic = Topic.GROUP, partitions = {"0", "1"})
            })
    public void consumer1_3(ConsumerRecord<String, Object> record) {
        System.out.println("consumer1-3 收到消息:" + record.value());
    }

    // 分组 2 中的消费者
    @KafkaListener(id = "consumer2-1", groupId = "group2", topics = Topic.GROUP)
    public void consumer2_1(ConsumerRecord<String, Object> record) {
        System.err.println("consumer2-1 收到消息:" + record.value());
    }
}

```

### 3.3 消息发送

发送消息时候指定主题的具体分区：

```java
@GetMapping("sendGroup")
public void sendGroup() {
    for (int i = 0; i < 4; i++) {
        // 第二个参数指定分区，第三个参数指定消息键 分区优先
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(Topic.GROUP, i % 4, "key", "hello group " + i);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.info("发送消息失败:" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> sendResult) {
                System.out.println("发送结果:" + sendResult.toString());
            }
        });
    }
}
```

测试结果：

```yaml
# 主要看每次发送结果中的 partition 属性，代表四次消息分别发送到了主题的0,1,2,3分区
发送结果:SendResult [producerRecord=ProducerRecord(topic=spring.boot.kafka.newGroup, partition=1, headers=RecordHeaders(headers = [], isReadOnly = true), key=key, value=hello group 1, timestamp=null), recordMetadata=spring.boot.kafka.newGroup-1@13]
发送结果:SendResult [producerRecord=ProducerRecord(topic=spring.boot.kafka.newGroup, partition=0, headers=RecordHeaders(headers = [], isReadOnly = true), key=key, value=hello group 0, timestamp=null), recordMetadata=spring.boot.kafka.newGroup-0@19]
发送结果:SendResult [producerRecord=ProducerRecord(topic=spring.boot.kafka.newGroup, partition=3, headers=RecordHeaders(headers = [], isReadOnly = true), key=key, value=hello group 3, timestamp=null), recordMetadata=spring.boot.kafka.newGroup-3@13]
发送结果:SendResult [producerRecord=ProducerRecord(topic=spring.boot.kafka.newGroup, partition=2, headers=RecordHeaders(headers = [], isReadOnly = true), key=key, value=hello group 2, timestamp=null), recordMetadata=spring.boot.kafka.newGroup-2@13]
# 消费者组2 接收情况
consumer2-1 收到消息:hello group 1
consumer2-1 收到消息:hello group 0
consumer2-1 收到消息:hello group 2
consumer2-1 收到消息:hello group 3
# 消费者1-1接收情况
consumer1-1 收到消息:hello group 1
consumer1-1 收到消息:hello group 0
# 消费者1-3接收情况
consumer1-3 收到消息:hello group 1
consumer1-3 收到消息:hello group 0
# 消费者1-2接收情况
consumer1-2 收到消息:hello group 3
consumer1-2 收到消息:hello group 2
```

### 3.4 测试结果

- 和 Kafka 原本的机制一样，多消费者组之间对于同一个主题的消费彼此之间互不影响；
- 和 Kafka 原本机制不一样的是，这里我们消费者 1-1 和消费 1-3 共同属于同一个消费者组，并且监听同样的分区，按照 Kafka 原本的机制，群组保证每个分区只能被同一个消费者组的一个消费者使用，但是按照 Spring 的方式实现消息监听后，被两个消费者都监听到了。



## 四、序列化与反序列化

用例采用的是第三方 fastjson 将实体类序列化为 Json 后发送。实现如下：

```java
/***
 * 发送消息体为 bean 的消息
 */
@GetMapping("sendBean")
public void sendBean() {
    Programmer programmer = new Programmer("xiaoming", 12, 21212.33f, new Date());
    producer.sendMessage(Topic.BEAN, JSON.toJSON(programmer).toString());
}

```

```java
@Component
@Slf4j
public class KafkaBeanConsumer {

    @KafkaListener(groupId = "beanGroup",topics = Topic.BEAN)
    public void consumer(ConsumerRecord<String, Object> record) {
        System.out.println("消费者收到消息:" + JSON.parseObject(record.value().toString(), Programmer.class));
    }
}
```


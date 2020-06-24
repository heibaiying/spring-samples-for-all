# Spring 整合 Redis （注解方式）
<nav>
<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11--Redis客户端">1.1  Redis客户端</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-可视化软件">1.2 可视化软件 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-项目结构">1.3 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#14-基本依赖">1.4 基本依赖</a><br/>
<a href="#二spring-整合-jedis">二、spring 整合 jedis</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-新建配置文件">2.1 新建配置文件</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-单机配置">2.2 单机配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-集群配置">2.3 集群配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-单机版本测试用例">2.4 单机版本测试用例</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#25-集群版本测试用例">2.5 集群版本测试用例</a><br/>
<a href="#三Spring-整合-Redisson">三、Spring 整合 Redisson</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-单机配置">2.1 单机配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-集群配置">2.2 集群配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-存储基本类型测试用例">2.3 存储基本类型测试用例</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-存储实体对象测试用例">2.4 存储实体对象测试用例</a><br/>
</nav>


## 一、项目说明

### 1.1  Redis客户端

关于 spring 整合 Redis 本用例提供两种整合方法：

- **Jedis**: 官方推荐的 java 客户端，能够胜任 Redis 的大多数基本使用；

- **Redisson**：也是官方推荐的客户端，比起 Redisson 提供了更多高级的功能，如分布式锁、集合数据切片等功能。同时提供了丰富而全面的中英文版本的说明文档。

 Redis 所有语言官方推荐的客户端可以在 [客户端](http://www.redis.cn/clients.html) 该网页查看，其中官方推荐的客户端使用了:star:进行标注：

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/redis官方推荐客户端.png"/> </div>


### 1.2 可视化软件 

推荐使用 **Redis Desktop Manager** 作为可视化查看工具，可以直观看到存储的数据及其序列化的情况。

### 1.3 项目结构

+ Jedis 和 Redisson 的配置类和单元测试分别位于 config 和 test 下对应的包中，其中集群的配置类以 cluster 开头。

+ 实体类 Programmer.java 用于测试 Redisson 序列化与反序列化。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-redis-annotation.png"/> </div>


### 1.4 基本依赖

除了 Spring 的基本依赖外，需要导入 Jedis 和 Redisson 对应的客户端依赖：

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.0.0</version>
</dependency>
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.9.1</version>
</dependency>
<!--redisson 中部分功能依赖了netty  -->
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.32.Final</version>
</dependency>
```



## 二、spring 整合 jedis

### 2.1 新建配置文件

新建配置文件及其映射类：

```properties
redis.host=127.0.0.1
redis.port=6379
# 连接超时时间
redis.timeout=2000
# 最大空闲连接数
redis.maxIdle=8
# 最大连接数
redis.maxTotal=16
```

```java
@Configuration
@PropertySource(value = "classpath:jedis.properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisProperty {

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.timeout}")
    private int timeout;
    @Value("${redis.maxIdle}")
    private int maxIdle;
    @Value("${redis.maxTotal}")
    private int maxTotal;
}
```

### 2.2 单机配置

```java
@Configuration
@ComponentScan(value = "com.heibaiying.*")
public class SingleJedisConfig {

    @Bean
    public JedisPool jedisPool(RedisProperty property) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(property.getMaxIdle());
        poolConfig.setMaxTotal(property.getMaxTotal());
        return new JedisPool(poolConfig, property.getHost(), property.getPort(), property.getTimeout());
    }

    @Bean(destroyMethod = "close")
    public Jedis jedis(JedisPool jedisPool) {
        return jedisPool.getResource();
    }
}
```

### 2.3 集群配置

```java
@Configuration
@ComponentScan(value = "com.heibaiying.*")
public class ClusterJedisConfig {

    @Bean
    public JedisCluster jedisCluster(RedisProperty property) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(property.getMaxIdle());
        poolConfig.setMaxTotal(property.getMaxTotal());
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        nodes.add(new HostAndPort("127.0.0.1", 6379));
        nodes.add(new HostAndPort("127.0.0.1", 6380));
        return new JedisCluster(nodes, 2000);
    }
}
```

### 2.4 单机版本测试用例

- 需要注意的是，对于 Jedis 而言，单机版本和集群版本注入的实例是不同的；

- Jedis 本身并不支持序列化于反序列化操作，如果需要存储实体类，需要序列化后存入，而 Redisson 本身就支持序列化于反序列化操作，详见下文)。

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SingleJedisConfig.class)
public class JedisSamples {

    @Autowired
    private Jedis jedis;

    @Test
    public void Set() {
        jedis.set("hello", "spring annotation");
    }

    @Test
    public void Get() {
        String s = jedis.get("hello");
        System.out.println(s);
    }

    @Test
    public void setEx() {
        String s = jedis.setex("spring", 10, "我会在 10 秒后过期");
        System.out.println(s);
    }

}
```

### 2.5 集群版本测试用例

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ClusterJedisConfig.class)
public class JedisClusterSamples {

    @Autowired
    private JedisCluster jedisCluster;

    @Test
    public void Set() {
        jedisCluster.set("hello", "spring");
    }

    @Test
    public void Get() {
        String s = jedisCluster.get("hello");
        System.out.println(s);
    }

    @Test
    public void setEx() {
        String s = jedisCluster.setex("spring", 10, "我会在 10 秒后过期");
        System.out.println(s);
    }
}
```



## 三、Spring 整合 Redisson

### 2.1 单机配置

```java
@Configuration
public class SingalRedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }
}
```

### 2.2 集群配置

```java
@Configuration
public class ClusterRedissonConfig {

    //@Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用 SSL 连接
                .addNodeAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:6380")
                .addNodeAddress("redis://127.0.0.1:6381");
        return Redisson.create(config);
    }
}
```

### 2.3 存储基本类型测试用例

需要注意的是，对于 Redisson 而言， 单机和集群在使用的时候注入的都是 RedissonClient，这和 Jedis 是不同的。

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SingalRedissonConfig.class)
public class RedissonSamples {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void Set() {
        // key 存在则更新 不存在则删除
        RBucket<String> rBucket = redissonClient.getBucket("redisson");
        rBucket.set("annotation Value");
        redissonClient.shutdown();
    }

    @Test
    public void Get() {
        // key 存在则更新 不存在则删除
        RBucket<String> rBucket = redissonClient.getBucket("redisson");
        System.out.println(rBucket.get());
    }

    @Test
    public void SetEx() {
        // key 存在则更新 不存在则删除
        RBucket<String> rBucket = redissonClient.getBucket("redissonEx");
        rBucket.set("我在十秒后会消失", 10, TimeUnit.SECONDS);
    }


    @After
    public void close() {
        redissonClient.shutdown();
    }
}
```

### 2.4 存储实体对象测试用例

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SingalRedissonConfig.class)
public class RedissonObjectSamples {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void Set() {
        RBucket<Programmer> rBucket = redissonClient.getBucket("programmer");
        rBucket.set(new Programmer("xiaoming", 12, 5000.21f, new Date()));
        redissonClient.shutdown();
        // Redisson 的对象编码类是用于将对象进行序列化和反序列化 默认采用 Jackson
        // 存储结果: {"@class":"com.heibaiying.com.heibaiying.bean.Programmer","age":12,"birthday":["java.util.Date",1545714986590],"name":"xiaoming","salary":5000.21}
    }

    @Test
    public void Get() {
        RBucket<Programmer> rBucket = redissonClient.getBucket("programmer");
        System.out.println(rBucket.get());
    }

    @After
    public void close() {
        redissonClient.shutdown();
    }
}
```

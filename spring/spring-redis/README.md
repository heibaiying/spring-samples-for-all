# Spring 整合 Redis （ XML配置方式）

<nav>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11--Redis客户端">1.1  Redis客户端</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-可视化软件">1.2 可视化软件 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-项目结构">1.3 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#14-基本依赖">1.4 基本依赖</a><br/>
<a href="#二Spring-整合-Jedis">二、Spring 整合 Jedis</a><br/>
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
### 一、项目说明

### 1.1  Redis客户端

关于 spring 整合 Redis 本用例提供两种整合方法：

+ **Jedis**: 官方推荐的 java 客户端，能够胜任 Redis 的大多数基本使用；

+ **Redisson**：也是官方推荐的客户端，比起 Redisson 提供了更多高级的功能，如分布式锁、集合数据切片等功能。同时提供了丰富而全面的中英文版本的说明文档。

 Redis 所有语言官方推荐的客户端可以在 [客户端](http://www.redis.cn/clients.html) 该网页查看，其中官方推荐的客户端使用了:star:进行标注：

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/redis官方推荐客户端.png"/> </div>


### 1.2 可视化软件 

推荐使用 **Redis Desktop Manager** 作为可视化查看工具，可以直观看到存储的数据及其序列化的情况。

### 1.3 项目结构

+ Jedis 和 Redisson 的配置和单元测试分别位于 resources 和 test 下对应的包中，其中集群的配置文件以 cluster 结尾。所有配置按需在 `springApplication.xml` 用 import 标签导入。

+ 实体类 Programmer 用于测试 Redisson 序列化与反序列化。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-redis.png"/> </div>


**springapplication.xml 文件：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--jedis 连接单机 redis-->
    <import resource="classpath:jedis/jedis.xml"/>

    <!--jedis 连接集群 redis-->
    <!--<import resource="classpath:jedis/jedisCluster.xml"/>-->

    <!--redisson 连接单机 redis-->
    <import resource="classpath:redisson/redisson.xml"/>

    <!--redisson 连接集群 redis-->
   <!--<import resource="classpath:redisson/redissonCluster.xml"/>-->

</beans>
```

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



## 二、Spring 整合 Jedis

### 2.1 新建配置文件

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

### 2.2 单机配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd">

    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:jedis/jedis.properties"/>

    <!--初始化连接池配置-->
    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxIdle" value="${redis.maxIdle}"/>
        <property name="maxTotal" value="${redis.maxTotal}"/>
    </bean>

    <!--配置 jedis 连接池-->
    <bean id="jedisPool" class="redis.clients.jedis.JedisPool">
        <constructor-arg name="poolConfig" ref="jedisPoolConfig"/>
        <constructor-arg name="host" value="${redis.host}"/>
        <constructor-arg name="port" value="${redis.port}"/>
        <constructor-arg name="timeout" value="${redis.timeout}"/>
    </bean>

    <!--把 jedis 创建与销毁交给 spring 来管理-->
    <bean id="jedis" factory-bean="jedisPool" factory-method="getResource" destroy-method="close"/>

</beans>
```

### 2.3 集群配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd">

    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:jedis/jedis.properties"/>

    <!--初始化连接池配置-->
    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxIdle" value="${redis.maxIdle}"/>
        <property name="maxTotal" value="${redis.maxTotal}"/>
    </bean>

    <!--配置 jedis 连接池 (集群)-->
    <bean id="jedisCluster" class="redis.clients.jedis.JedisCluster">
        <constructor-arg name="nodes">
            <set>
                <bean class="redis.clients.jedis.HostAndPort">
                    <constructor-arg name="host" value="127.0.0.1"/>
                    <constructor-arg name="port" value="6379"/>
                </bean>
                <bean class="redis.clients.jedis.HostAndPort">
                    <constructor-arg name="host" value="127.0.0.1"/>
                    <constructor-arg name="port" value="6380"/>
                </bean>
                <bean class="redis.clients.jedis.HostAndPort">
                    <constructor-arg name="host" value="127.0.0.1"/>
                    <constructor-arg name="port" value="6381"/>
                </bean>
            </set>
        </constructor-arg>
        <constructor-arg name="timeout" value="${redis.timeout}"/>
    </bean>

</beans>
```

### 2.4 单机版本测试用例

+ 需要注意的是，对于 Jedis 而言，单机版本和集群版本注入的实例是不同的；

+ Jedis 本身并不支持序列化于反序列化操作，如果需要存储实体类，需要序列化后存入，而 Redisson 本身就支持序列化于反序列化操作，详见下文)。

```java
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
public class JedisSamples {

    @Autowired
    private Jedis jedis;

    @Test
    public void Set() {
        jedis.set("hello", "spring");
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
@ContextConfiguration({"classpath:springApplication.xml"})
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

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:redisson="http://redisson.org/schema/redisson"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://redisson.org/schema/redisson
       http://redisson.org/schema/redisson/redisson.xsd">


    <redisson:client>
        <!--更多可配置项见官方文档 2.6.2. 通过 JSON、YAML 和 Spring XML 文件配置单节点模式
         <a href="https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95#26-%E5%8D%95redis%E8%8A%82%E7%82%B9%E6%A8%A1%E5%BC%8F"> -->
        <redisson:single-server
                address="redis://127.0.0.1:6379"
                idle-connection-timeout="10000"
                ping-timeout="1000"
                connect-timeout="10000"
                timeout="3000"
                retry-attempts="3"
                retry-interval="1500"
                connection-minimum-idle-size="10"
                connection-pool-size="64"
                database="0"
        />
    </redisson:client>

</beans>
```

### 2.2 集群配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:redisson="http://redisson.org/schema/redisson"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
          http://redisson.org/schema/redisson http://redisson.org/schema/redisson/redisson.xsd">

    <!-- 最基本配置 -->
    <redisson:client>
        <!--集群更多配置参数见官方文档 2.4.2 通过 JSON、YAML 和 Spring XML 文件配置集群模式
         <a href="https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95#24-%E9%9B%86%E7%BE%A4%E6%A8%A1%E5%BC%8F"> -->
        <redisson:cluster-servers>
            <redisson:node-address value="redis://127.0.0.1:6379"/>
            <redisson:node-address value="redis://127.0.0.1:6380"/>
            <redisson:node-address value="redis://127.0.0.1:6381"/>
        </redisson:cluster-servers>
    </redisson:client>
</beans>
```

### 2.3 存储基本类型测试用例

需要注意的是，对于 Redisson 而言， 单机和集群在使用的时候注入的都是 RedissonClient，这和 Jedis 是不同的。

```java
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
public class RedissonSamples {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void Set() {
        // key 存在则更新 不存在则删除
        RBucket<String> rBucket = redissonClient.getBucket("redisson");
        rBucket.set("firstValue");
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
@ContextConfiguration({"classpath:springApplication.xml"})
public class RedissonObjectSamples {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void Set() {
        RBucket<Programmer> rBucket = redissonClient.getBucket("programmer");
        rBucket.set(new Programmer("xiaoming", 12, 5000.21f, new Date()));
        // Redisson 默认采用 Jackson 将对象进行序列化和反序列化 
        // 存储结果: {"@class":"com.heibaiying.bean.Programmer","age":12,"birthday":["java.util.Date",1545714986590],"name":"xiaoming","salary":5000.21}
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


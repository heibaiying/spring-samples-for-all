# spring 整合 redis （注解方式）

## 一、说明

### 1.1  Redis 客户端说明

关于spring 整合 mybatis 本用例提供两种整合方法：

1. jedis: 官方推荐的java客户端，能够胜任redis的大多数基本使用；
2. redisson：也是官方推荐的客户端，比起jedis提供了更多高级的功能，比如分布式锁、集合数据切片等功能。同时提供了丰富而全面的中英文版本的wiki。

注：关于redis其他语言官方推荐的客户端可以在[客户端](http://www.redis.cn/clients.html)该网页查看，其中官方推荐的用了黄色星星:star:标注。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/redis官方推荐客户端.png"/> </div>



### 1.2 Redis可视化软件 

推荐**Redis Desktop Manager** 作为可视化查看工具，可以直观看到用例中测试关于存储实体对象序列化的情况。

### 1.3 项目结构说明

1. jedis和redisson的配置类和单元测试分别位于config和test下对应的包中，其中集群的配置类以cluster开头。
2. 实体类Programmer.java用于测试Redisson序列化与反序列化

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-redis-annotation.png"/> </div>



### 1.3 依赖说明

除了spring的基本依赖外，需要导入jedis 和 redisson 对应的客户端依赖包

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

#### 2.1 新建基本配置文件和其映射类

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



#### 2.2 单机配置

```java
/**
 * @author : heibaiying
 * @description : Jedis 单机配置
 */
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

#### 2.3 集群配置

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

#### 2.4 单机版本测试用例

1.需要注意的是，对于jedis而言，单机版本和集群版本注入的实例是不同的；

2.jedis本身并不支持序列化于反序列化操作，如果需要存储实体类，需要序列化后存入。(redisson本身就支持序列化于反序列化，详见下文)

```java
/**
 * @author : heibaiying
 * @description :redis 单机版测试
 */
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
        String s = jedis.setex("spring", 10, "我会在10秒后过期");
        System.out.println(s);
    }

}
```

#### 2.5 集群版本测试用例

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
        String s = jedisCluster.setex("spring", 10, "我会在10秒后过期");
        System.out.println(s);
    }


}
```



## 三、spring 整合 redisson

#### 2.1 单机配置

```java
/**
 * @author : heibaiying
 * @description : redisson 单机配置
 */
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

#### 2.2 集群配置

```java
/**
 * @author : heibaiying
 * @description : redisson 集群配置
 */
@Configuration
public class ClusterRedissonConfig {

    //@Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:6380")
                .addNodeAddress("redis://127.0.0.1:6381");
        return Redisson.create(config);
    }

}
```

#### 2.3 存储基本类型测试用例

1. 这里需要注意的是，对于Redisson而言， 单机和集群最后在使用的时候注入的都是RedissonClient，这和jedis是不同的。

```java
/**
 * @author : heibaiying
 * @description :redisson 操作普通数据类型
 */

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

#### 2.4 存储实体对象测试用例

```java
/**
 * @author : heibaiying
 * @description :redisson 对象序列化与反序列化
 */


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SingalRedissonConfig.class)
public class RedissonObjectSamples {

    @Autowired
    private RedissonClient redissonClient;

    // Redisson的对象编码类是用于将对象进行序列化和反序列化 默认采用Jackson

    @Test
    public void Set() {
        RBucket<Programmer> rBucket = redissonClient.getBucket("programmer");
        rBucket.set(new Programmer("xiaoming", 12, 5000.21f, new Date()));
        redissonClient.shutdown();
        //存储结果: {"@class":"com.heibaiying.com.heibaiying.bean.Programmer","age":12,"birthday":["java.util.Date",1545714986590],"name":"xiaoming","salary":5000.21}
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

## 附：Redis的数据结构和操作命令

### 1.1 预备

#### 1.1.1 全局命令

1. 查看所有键： **keys \*** 

2. 查看键总数：**dbsize**  

3. 检查键是否存在：**exists key**

4. 删除键：**del key [key ...]**   支持删除多个键

5. 键过期：**expire key seconds**

   ttl命令会返回键的剩余过期时间， 它有3种返回值：

   - 大于等于0的整数： 键剩余的过期时间。
   - -1： 键没设置过期时间。
   - -2： 键不存在 

6. 键的数据结构 **type key**

#### 1.1.2 数据结构和内部编码

type命令实际返回的就是当前键的数据结构类型， 它们分别是：**string**（字符串） 、 **hash**（哈希） 、 **list**（列表） 、 **set**（集合） 、 **zset**（有序集合） 

#### 1.1.3 单线程架构

1. 纯内存访问， Redis将所有数据放在内存中， 内存的响应时长大约为100纳秒， 这是Redis达到每秒万级别访问的重要基础。
2. 非阻塞I/O， Redis使用epoll作为I/O多路复用技术的实现， 再加上Redis自身的事件处理模型将epoll中的连接、 读写、 关闭都转换为事件， 不在网络I/O上浪费过多的时间， 如图2-6所示。 
3. 单线程避免了线程切换和竞态产生的消耗。 

### 1.2 字符串

| 作用                   | 格式                                                         | 参数或示例                                                   |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 设置值                 | set key value \[ex seconds]\[px milliseconds][nx\|xx] setnx setex | ex seconds： 为键设置秒级过期时间。 <br/>px milliseconds： 为键设置毫秒级过期时间。<br/>nx： 键必须不存在， 才可以设置成功， 用于添加。<br/>xx： 与nx相反， 键必须存在， 才可以设置成功， 用于更新。 |
| 获取值                 | get key                                                      | r如果获取的键不存在 ，则返回nil(空)                          |
| 批量设置               | mset key value [key value ...]                               | mset a 1 b 2 c 3 d 4                                         |
| 批量获取值             | mget key [key ...]                                           | mget a b c d                                                 |
| 计数                   | incr key decr key incrby key increment（指定数值自增）<br/>decrby key decrement（指定数值自减）<br/>incrbyfloat key increment （浮点数自增） | 值不是整数， 返回错误。 值是整数， 返回自增或自减后的结果。<br/>键不存在，创建键，并按照值为0自增或自减， 返回结果为1。 |
| 追加值                 | append key value                                             | 向字符串的默认追加值                                         |
| 字符串长度             | strlen key                                                   | 获取字符串长度，中文占用三个字节                             |
| 设置并返回原值         | getset key value                                             |                                                              |
| 设置指定位置的租字符串 | setrange key offeset value                                   |                                                              |
| 获取部分字符串         | getrange key start end                                       |                                                              |

### 1.3 哈希

| 作用                      | 格式                                                         | 参数或示例                                                   |
| ------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 设置值                    | hset key field value                                         | hset user:1 name tom<br/>hset user:1 age 12                  |
| 获取值                    | hget key field                                               | hget user:1 name                                             |
| 删除field                 | hdel key field [field ...]                                   |                                                              |
| 计算field个数             | hlen key                                                     |                                                              |
| 批量设置或获取field-value | hmget key field [field]<br/>hmset key field value [field value...] | hmset user:1 name mike age 12 city tianjin<br/>hmget user:1 name city |
| 判断field是否存在         | hexists key field                                            |                                                              |
| 获取所有field             | hkeys key                                                    |                                                              |
| 获取所有value             | hvals key                                                    |                                                              |
| 获取所有的filed-value     | hgetall key                                                  | 如果哈希元素个数比较多， 会存在阻塞Redis的可能。<br/>获取全部 可以使用hscan命令， 该命令会渐进式遍历哈希类型 |
| 计数                      | hincrby key field<br/>hincrbyfloat key field                 |                                                              |

### 1.4 列表

| 作用     | 格式                                                         | 参数或示例                                                   |
| -------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 增       | 左侧插入：lpush key value [value ...] 右侧插入：rpush key value [value ...] 某个指定元素前后插入：linsert key before\|after pivot value |                                                              |
| 查       | 获取指定范围内的元素列表：lrange key start end 获取列表指定索引下标的元素：lindex key index 获取列表指定长度：llen key | lrange listkey 0 -1                                          |
| 删       | 从列表左侧弹出元素：lpop key 从列表右侧弹出元素：rpop key 删除指定元素：lrem key count value 截取列表：ltrim key start end | count>0， 从左到右， 删除最多count个元素。<br/>count<0， 从右到左， 删除最多count绝对值个元素。<br/>count=0， 删除所有 |
| 改       | 修改指定索引下标的元素：lset key index newValue              |                                                              |
| 阻塞操作 | blpop key [key ...] timeout brpop key [key ...] timeout      | key[key...]： 多个列表的键。 timeout： 阻塞时间\|等待时间（单位： 秒） |



### 1.5 集合

集合（set） 类型也是用来保存多个的字符串元素， 但和列表类型不一样的是， **集合中不允许有重复元素**， 并且集合中的元素是无序的， **不能通过索引下标获取元素**。  

**集合内操作**：

| 作用                 | 格式                           | 参数或示例                                |
| -------------------- | ------------------------------ | ----------------------------------------- |
| 添加元素             | sadd key element [element ...] | 返回结果为添加成功的元素个数              |
| 删除元素             | srem key element [element ...] | 返回结果为成功删除的元素个数              |
| 计算元素个数         | scard key                      |                                           |
| 判断元素是否在集合中 | sismember key element          |                                           |
| 随机返回             | srandmember key [count]        | 随机从集合返回指定个数元素，count 默认为1 |
| 从集合随机弹出元素   | spop key                       | srandmember 不会从集合中删除元素，spop 会 |
| 获取集合中所有元素   | smembers key                   | 可用sscan 代替                            |

**集合间操作**：

| 作用                         | 格式                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| 求多个集合的交集             | sinter key [key ...]                                         |
| 求多个集合的并集             | suinon key [key ...]                                         |
| 求多个集合的差集             | sdiff key [key ...]                                          |
| 将交集、并集、差集的结果保存 | sinterstore destination key [key ...] <br/>suionstore destination key [key ...]<br/>sdiffstore destination key [key ...] |

### 1.6 有序集合

有序集合中的元素可以排序。 但是它和列表使用索引下标作为排序依据不同的是， 它给每个元素设置一个分数（score） 作为排序的依据。  

**集合内操作**：

| 作用                     | 格式                                                         | 参数或示例                                                   |
| ------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 添加成员                 | zadd key score member [score member ...]                     | nx： member必须不存在， 才可设置成功， 用于添加。<br> xx： member必须存在， 才可以设置成功， 用于更新。<br/>ch： 返回此次操作后， 有序集合元素和分数发生变化的个数<br/>incr： 对score做增加， 相当于后面介绍的zincrby。 |
| 计算成员个数             | zcard key                                                    |                                                              |
| 计算某个成员的分数       | zscore key member                                            |                                                              |
| 计算某个成员的排名       | zrank key member  zrevrank key member                        | zrank是从分数从低到高返回排名， zrevrank反之。               |
| 删除成员                 | zrem key member [member ...]                                 |                                                              |
| 增加成员分数             | zincrby key increment member                                 | zincrby user:ranking 9 tom                                   |
| 返回指定排名范围的成员   | zrange key start end [withscores] zrange key start end [withscores] | zrange是从低到高返回， zrevrange反之。                       |
| 返回指定分数范围内的成员 | zrangebyscore key min max \[withscores][limit offset count] zrevrangebyscore key max min \[withscores][limit offset count] | 其中zrangebyscore按照分数从低到高返回， zrevrangebyscore反之。 [limit offset count]选项可以限制输出的起始位置和个数： 同时min和max还支持开区间（小括号） 和闭区间（中括号） ， -inf和+inf分别代表无限小和无限大 |
| 删除指定排名内的升序元素 | zremrangerank key start end                                  |                                                              |
| 删除指定分数范围的成员   | zremrangebyscore key min max                                 |                                                              |

**集合间操作**：

| 作用 | 格式                                                         |
| ---- | ------------------------------------------------------------ |
| 交集 | zinterstore destination numkeys key \[key ...]  [weights weight [weight ...]] \[aggregate sum\|min\|max] |
| 并集 | zunionstore destination numkeys key \[key ...] [weights weight [weight ...]] \[aggregate sum\|min\|max] |

- destination： 交集计算结果保存到这个键。
- numkeys： 需要做交集计算键的个数。
- key[key...]： 需要做交集计算的键。 
- weights weight[weight...]： 每个键的权重， 在做交集计算时， 每个键中的每个member会将自己分数乘以这个权重， 每个键的权重默认是1。
- aggregate sum|min|max： 计算成员交集后， 分值可以按照sum（和） 、min（最小值） 、 max（最大值） 做汇总， 默认值是sum。 

### 1.7 键管理

#### 1.7.1 单个键管理

##### 1.键重命名  

**rename key newkey** 

 为了防止被强行rename， Redis提供了renamenx命令， 确保只有newKey不存在时候才被覆盖。

##### 2. 随机返回键 

 **random  key**

##### 3.键过期

- expire key seconds： 键在seconds秒后过期。
- expireat key timestamp： 键在秒级时间戳timestamp后过期。 
- pexpire key milliseconds： 键在milliseconds毫秒后过期。
- pexpireat key milliseconds-timestamp键在毫秒级时间戳timestamp后过期 

注意：

1. 如果expire key的键不存在， 返回结果为0 
2. 如果设置过期时间为负值， 键会立即被删除， 犹如使用del命令一样 
3. persist  key  t命令可以将键的过期时间清除 
4. 对于字符串类型键， 执行set命令会去掉过期时间， 这个问题很容易在开发中被忽视 
5. Redis不支持二级数据结构（例如哈希、 列表） 内部元素的过期功能， 例如不能对列表类型的一个元素做过期时间设置
6. setex命令作为set+expire的组合， 不但是原子执行， 同时减少了一次网络通讯的时间  

#### 1.7.2 键遍历

##### 1. 全量键遍历

**keys pattern** 

##### 2. 渐进式遍历

scan cursor \[match pattern] \[count number] 

- cursor是必需参数， 实际上cursor是一个游标， 第一次遍历从0开始， 每次scan遍历完都会返回当前游标的值， 直到游标值为0， 表示遍历结束。
- match pattern是可选参数， 它的作用的是做模式的匹配， 这点和keys的模式匹配很像。
- count number是可选参数， 它的作用是表明每次要遍历的键个数， 默认值是10， 此参数可以适当增大。 

#### 1.7.3 数据库管理

##### 1.切换数据库

**select dbIndex**

##### 2.flushdb/flushall 

flushdb/flushall命令用于清除数据库， 两者的区别的是flushdb只清除当前数据库， flushall会清除所有数据库。 
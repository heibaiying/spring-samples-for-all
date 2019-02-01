# spring boot 整合 redis
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目主要依赖">1.2 项目主要依赖</a><br/>
<a href="#二整合-Redis">二、整合 Redis</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-在applicationyml-中配置redis数据源">2.1 在application.yml 中配置redis数据源</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22--封装redis基本操作">2.2  封装redis基本操作</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-redisTemplate-序列化为json格式与反序列化">2.3 redisTemplate 序列化为json格式与反序列化</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-测试">2.4 测试</a><br/>
<a href="#附Redis的数据结构和操作命令">附：Redis的数据结构和操作命令</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-预备">1.1 预备</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#111-全局命令">1.1.1 全局命令</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#112-数据结构和内部编码">1.1.2 数据结构和内部编码</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#113-单线程架构">1.1.3 单线程架构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-字符串">1.2 字符串</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-哈希">1.3 哈希</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#14-列表">1.4 列表</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#15-集合">1.5 集合</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#16-有序集合">1.6 有序集合</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#17-键管理">1.7 键管理</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#171-单个键管理">1.7.1 单个键管理</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1键重命名">1.键重命名  </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#2-随机返回键">2. 随机返回键 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#3键过期">3.键过期</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#172-键遍历">1.7.2 键遍历</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1-全量键遍历">1. 全量键遍历</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#2-渐进式遍历">2. 渐进式遍历</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#173-数据库管理">1.7.3 数据库管理</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1切换数据库">1.切换数据库</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#2flushdb/flushall">2.flushdb/flushall </a><br/>
## 正文<br/>


## 一、说明

#### 1.1 项目结构

1. RedisConfig.java实现了redisTemplate 序列化与反序列化的配置；
2. RedisOperation和RedisObjectOperation分别封装了对基本类型和对象的操作。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-redis.png"/> </div>

#### 1.2 项目主要依赖

```xml
<!--redis starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<!--jackson 序列化包 -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.8</version>
</dependency>
```



## 二、整合 Redis

#### 2.1 在application.yml 中配置redis数据源

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    # 默认采用的也是 0 号数据库 redis官方在4.0之后版本就不推荐采用单节点多数据库(db1-db15)的方式存储数据，如果有需要应该采用集群方式构建
    database: 0

# 如果是集群节点 采用如下配置指定节点
#spring.redis.cluster.nodes
```

#### 2.2  封装redis基本操作

需要说明的是spring boot 提供了两个template 用于操作redis:

- StringRedisTemplate：由于redis在大多数使用情况下都是操作字符串类型的存储，所以spring boot 将对字符串的操作单独封装在StringRedisTemplate ；
- RedisTemplate<Object, Object> ：redis 用于操作任意类型的template。

```java
/**
 * @author : heibaiying
 * @description : redis 基本操作
 */

@Component
public class RedisOperation {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /***
     * 操作普通字符串
     */
    public void StringSet(String key, String value) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    /***
     * 操作列表
     */
    public void ListSet(String key, List<String> values) {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        values.forEach(value -> listOperations.leftPush(key, value));
    }

    /***
     * 操作集合
     */
    public void SetSet(String key, Set<String> values) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        values.forEach(value -> setOperations.add(key, value));
    }

    /***
     * 获取字符串
     */
    public String StringGet(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /***
     * 列表弹出元素
     */
    public String ListLeftPop(String key) {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        return listOperations.leftPop(key, 2, TimeUnit.SECONDS);
    }

    /***
     * 集合弹出元素
     */
    public String SetPop(String key) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        return setOperations.pop(key);
    }

}
```

```java
/**
 * @author : heibaiying
 * @description : redis 基本操作
 */

@Component
public class RedisObjectOperation {

    @Autowired
    private RedisTemplate<Object, Object> objectRedisTemplate;

    /***
     * 操作对象
     */
    public void ObjectSet(Object key, Object value) {
        ValueOperations<Object, Object> valueOperations = objectRedisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    /***
     * 操作元素为对象列表
     */
    public void ListSet(Object key, List<Object> values) {
        ListOperations<Object, Object> listOperations = objectRedisTemplate.opsForList();
        values.forEach(value -> listOperations.leftPush(key, value));
    }

    /***
     * 操作元素为对象集合
     */
    public void SetSet(Object key, Set<Object> values) {
        SetOperations<Object, Object> setOperations = objectRedisTemplate.opsForSet();
        values.forEach(value -> setOperations.add(key, value));
    }

    /***
     * 获取对象
     */
    public Object ObjectGet(Object key) {
        ValueOperations<Object, Object> valueOperations = objectRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /***
     * 列表弹出元素
     */
    public Object ListLeftPop(Object key) {
        ListOperations<Object, Object> listOperations = objectRedisTemplate.opsForList();
        return listOperations.leftPop(key, 2, TimeUnit.SECONDS);
    }

    /***
     * 集合弹出元素
     */
    public Object SetPop(Object key) {
        SetOperations<Object, Object> setOperations = objectRedisTemplate.opsForSet();
        return setOperations.pop(key);
    }

}
```

#### 2.3 redisTemplate 序列化为json格式与反序列化

这里需要说明的spring boot 的 redisTemplate 本身是实现了对象的序列化与反序列化的，是支持直接存取对象的。但是这里的序列化默认采用的是JdkSerializationRedisSerializer.serialize()序列化为二进制码，这个本身是不影响redisTemplate 的操作的，因为redisTemplate在取出数据的时候会自动进行反序列化。

但是如果我们在命令行中使用get命令去获取数据时候，得到的是一串不直观的二进制码，所以我们尽量将其序列化为直观的json格式存储。

```java
/**
 * @author : heibaiying
 * @description : 自定义序列化器
 * 不定义的话默认采用的是serializer.JdkSerializationRedisSerializer.serialize()序列化为二进制字节码 存储在数据库中不直观
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用Jackson2JsonRedisSerialize  需要导入依赖 com.fasterxml.jackson.core jackson-databind
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        // 第一个参数表示: 表示所有访问者都受到影响 包括 字段, getter / isGetter,setter，creator
        // 第二个参数表示: 所有类型的访问修饰符都是可接受的，不论是公有还有私有表变量都会被序列化
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置key,value 序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```

#### 2.4 测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisOperation redisOperation;

    @Test
    public void StringOperation() {
        redisOperation.StringSet("hello", "redis");
        String s = redisOperation.StringGet("hello");
        Assert.assertEquals(s, "redis");
    }

    @Test
    public void ListOperation() {
        redisOperation.ListSet("skill", Arrays.asList("java", "oracle", "vue"));
        String s = redisOperation.ListLeftPop("skill");
        Assert.assertEquals(s, "vue");
    }

    /*
     * 需要注意的是Redis的集合（set）不仅不允许有重复元素，并且集合中的元素是无序的，
     * 不能通过索引下标获取元素。哪怕你在java中传入的集合是有序的newLinkedHashSet，但是实际在Redis存储的还是无序的集合
     */
    @Test
    public void SetOperation() {
        redisOperation.SetSet("skillSet", Sets.newLinkedHashSet("java", "oracle", "vue"));
        String s = redisOperation.SetPop("skillSet");
        Assert.assertNotNull(s);
    }

}
```

<br/>

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
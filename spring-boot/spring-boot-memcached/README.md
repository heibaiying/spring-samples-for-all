# Spring Boot 整合 Memcached



<nav>
<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11--XMemcached">1.1  XMemcached</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目结构">1.2 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-基本依赖">1.3 基本依赖</a><br/>
<a href="#二整合-Memcached">二、整合 Memcached</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-单机配置">2.1 单机配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-集群配置">2.2 集群配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-存储基本类型">2.3 存储基本类型</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-存储实体对象">2.4 存储实体对象</a><br/>
<a href="#附Memcached-基本命令">附：Memcached 基本命令</a><br/>
</nav>

## 一、项目说明

### 1.1  XMemcached

Spring 官方并没有提供关于 Memcached 的 starter，所以我们还是采用 XMemcached 作为客户端进行整合。XMemcached 是基于 Java NIO 的 Memcached 高性能客户端，支持完整的 Memcached 协议，支持客户端分布并且提供了一致性哈希 (consistent hash) 算法的实现。

### 1.2 项目结构

Memcached 的整合配置位于 config 文件夹下：

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-memcached.png"/> </div>
### 1.3 基本依赖

```xml
 <!--memcached java 客户端-->
<dependency>
    <groupId>com.googlecode.xmemcached</groupId>
    <artifactId>xmemcached</artifactId>
    <version>2.4.5</version>
</dependency>
```



## 二、整合 Memcached

### 2.1 单机配置

```java
@Bean
public MemcachedClient memcachedClient() {
    XMemcachedClientBuilder builder = new XMemcachedClientBuilder("192.168.200.201:11211");
    MemcachedClient memcachedClient = null;
try {
	memcachedClient = builder.build();
	} catch (IOException e) {
	e.printStackTrace();
}
	return memcachedClient;
}
```

### 2.2 集群配置

```java
@Bean
public MemcachedClient memcachedClientForCluster() {

    List<InetSocketAddress> addressList = new ArrayList<InetSocketAddress>();
    addressList.add(new InetSocketAddress("192.168.200.201", 11211));
    addressList.add(new InetSocketAddress("192.168.200.201", 11212));
    // 赋予权重
    int[] weights = {1, 2};
    XMemcachedClientBuilder builder = new XMemcachedClientBuilder(addressList, weights);
    // 设置连接池大小
    builder.setConnectionPoolSize(10);
    // 协议工厂
    builder.setCommandFactory(new TextCommandFactory());
    // 分布策略，一致性哈希 KetamaMemcachedSessionLocator 或者 ArraySessionLocator(默认)
    builder.setSessionLocator(new KetamaMemcachedSessionLocator());
    // 设置序列化器
    builder.setTranscoder(new SerializingTranscoder());
    MemcachedClient memcachedClient = null;
    try {
        memcachedClient = builder.build();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return memcachedClient;
}
```

### 2.3 存储基本类型

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemSamples {

    @Autowired
    private MemcachedClient memcachedClient;

    @Test
    public void operate() throws InterruptedException, MemcachedException, TimeoutException {
        memcachedClient.set("hello", 0, "Hello,cluster xmemcached");
        String value = memcachedClient.get("hello");
        System.out.println("hello=" + value);
        memcachedClient.delete("hello");
        value = memcachedClient.get("hello");
        System.out.println("hello=" + value);
    }
}
```

### 2.4 存储实体对象

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemObjectSamples {

    @Autowired
    private MemcachedClient memcachedClient;

    @Test
    public void operate() throws InterruptedException, MemcachedException, TimeoutException {
        memcachedClient.set("programmer", 0, new Programmer("xiaoming", 12, 5000.21f, new Date()));
        Programmer programmer = memcachedClient.get("programmer");
        System.out.println("hello ," + programmer.getName());
        memcachedClient.delete("programmer");
        programmer = memcachedClient.get("programmer");
        Assert.assertNull(programmer);
    }
}

```



## 附：Memcached 基本命令

| 命令            | 格式                                               | 说明                                  |
| --------------- | -------------------------------------------------- | ------------------------------------- |
| 新增 set        | set  key  flags   exTime  length -> value          | 无论什么情况，都可以插入              |
| 新增 add        | add key  flags   exTime  length -> value           | 只有当 key 不存在的情况下，才可以插入   |
| 替换 replace    | replace  key  flags   exTime  length -> value      | 只修改已存在 key 的 value 值              |
| 追加内容 append  | append  key  flags   exTime  length -> value       | length 表示追加的长度而不是总长度      |
| 前面追加 prepend | prepend  key  flags   exTime  length -> value      | length 表示追加的长度而不是总长度      |
| 查询操作 get    | get  key                                           |                                       |
| 检查更新 cas    | cas  key  flags  exTime  length  version  -> value | 版本正确才更新                        |
| 详细获取 gets   | gets   key                                         | 返回的最后一个数代表 key 的 CAS 令牌  |
| 删除 delete     | delete   key                                       | 将数据打一个删除标记                  |
| 自增 incr       | incr  key  增加偏移量                              | incr 和 decr 只能操作能转换为数字的 Value |
| 自减 decr       | decr  key  减少偏移量                              | desr 不能将数字减少至 0 以下             |
| 清库            | flush_all                                          |                                       |

# Spring 整合 Mecached（XML配置方式）

<nav>
<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11--XMemcached">1.1  XMemcached</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目结构">1.2 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-相关依赖">1.3 相关依赖</a><br/>
<a href="#二整合-XMemcached">二、整合 XMemcached</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-单机配置">2.1 单机配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-集群配置">2.2 集群配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-存储基本类型测试用例">2.3 存储基本类型测试用例</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-存储实体对象测试用例">2.4 存储实体对象测试用例</a><br/>
<a href="#附Memcached-基本命令">附：Memcached 基本命令</a><br/>
</nav>

## 一、项目说明

### 1.1  XMemcached

XMemcached 是基于 Java NIO 实现的 Memcached 的高性能客户端，支持完整的 Memcached 协议，支持客户端分布并且提供了一致性哈希 (consistent hash) 算法的实现。

### 1.2 项目结构

- Memcached 的整合配置位于 resources 下的 memcached 文件夹下，其中集群配置以 cluster 开头。所有配置按需在 `springApplication.xml` 中用 import 标签导入。
- 实体类 Programmer 用于测试 Memcached 的序列化与反序列化。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-memcached.png"/> </div>
**springapplication.xml 文件：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--memcached 单机版配置-->
    <!--<import resource="classpath*:memcached/singleConfig.xml"/>-->

    <!--memcached 集群配置-->
    <import resource="classpath*:memcached/clusterConfig.xml"/>

</beans>
```

### 1.3 相关依赖

除了 Spring 的基本依赖外，需要导入 xmemcached 依赖包：

```xml
 <!--memcached java 客户端-->
<dependency>
    <groupId>com.googlecode.xmemcached</groupId>
    <artifactId>xmemcached</artifactId>
    <version>2.4.5</version>
</dependency>
```



## 二、整合 XMemcached

### 2.1 单机配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="memcachedClientBuilder" class="net.rubyeye.xmemcached.XMemcachedClientBuilder">
        <constructor-arg name="addressList" value="192.168.200.201:11211"/>
    </bean>

    <bean id="memcachedClient" factory-bean="memcachedClientBuilder" factory-method="build"
          destroy-method="shutdown"/>

</beans>
```

### 2.2 集群配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean name="memcachedClientBuilder" class="net.rubyeye.xmemcached.XMemcachedClientBuilder">
        <!--memcached servers 节点列表-->
        <constructor-arg name="addressList">
            <list>
                <bean class="java.net.InetSocketAddress">
                    <constructor-arg value="192.168.200.201"/>
                    <constructor-arg value="11211"/>
                </bean>
                <bean class="java.net.InetSocketAddress">
                    <constructor-arg value="192.168.200.201"/>
                    <constructor-arg value="11212"/>
                </bean>
            </list>
        </constructor-arg>
        <!--与 servers 对应的节点的权重-->
        <constructor-arg name="weights">
            <list>
                <value>1</value>
                <value>2</value>
            </list>
        </constructor-arg>
        <!--连接池大小-->
        <property name="connectionPoolSize" value="10"/>
        <!--协议工厂-->
        <property name="commandFactory">
            <bean class="net.rubyeye.xmemcached.command.TextCommandFactory"/>
        </property>
        <!--分布策略，一致性哈希 net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator 或者 ArraySessionLocator(默认)-->
        <property name="sessionLocator">
            <bean class="net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator"/>
        </property>
        <!--序列化转换器，默认使用 net.rubyeye.xmemcached.transcoders.SerializingTranscoder-->
        <property name="transcoder">
            <bean class="net.rubyeye.xmemcached.transcoders.SerializingTranscoder"/>
        </property>
    </bean>
    <!-- 集群配置 实例化 bean -->
    <bean name="memcachedClientForCulster" factory-bean="memcachedClientBuilder"
          factory-method="build" destroy-method="shutdown"/>
</beans>
```

### 2.3 存储基本类型测试用例

XMemcached  单机版和集群版注入的实例是完全相同的：

```java
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
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

### 2.4 存储实体对象测试用例

```java
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
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

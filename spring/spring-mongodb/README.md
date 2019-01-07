# spring 整合 mongodb（xml配置方式）

## 一、说明

### 1.1 项目结构说明

配置文件位于resources下,项目以单元测试的方式进行测试。

![spring+redis项目目录结构](D:\spring-samples-for-all\pictures\spring-mongodb.png)



### 1.2 依赖说明

除了spring的基本依赖外，需要导入mongodb整合依赖包

```xml
 <!--spring mongodb 整合依赖-->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>2.1.3.RELEASE</version>
</dependency>
```



## 二、spring mongodb

#### 2.1 新建配置文件

```properties
mongo.host=192.168.200.228
mongo.port=27017
# 数据库名称. 默认是'db'.
mongo.dbname=database
# 每个主机允许的连接数
mongo.connectionsPerHost=10
# 线程队列数，它和上面connectionsPerHost值相乘的结果就是线程队列最大值。如果连接线程排满了队列就会抛出异常
mongo.threadsAllowedToBlockForConnectionMultiplier=5
# 连接超时的毫秒 0是默认值且无限大。
mongo.connectTimeout=1000
# 最大等待连接的线程阻塞时间 默认是120000 ms (2 minutes).
mongo.maxWaitTime=1500
# 保持活动标志，控制是否有套接字保持活动超时 官方默认为true 且不建议禁用
mongo.socketKeepAlive=true
# 用于群集心跳的连接的套接字超时。
mongo.socketTimeout=1500
```

#### 2.2 整合配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mongo="http://www.springframework.org/schema/data/mongo"
       xsi:schemaLocation=
               "http://www.springframework.org/schema/context
          http://www.springframework.org/schema/context/spring-context.xsd
          http://www.springframework.org/schema/data/mongo http://www.springframework.org/schema/data/mongo/spring-mongo.xsd
          http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--扫描配置文件-->
    <context:property-placeholder location="classpath:mongodb.properties"/>

    <!--定义用于访问MongoDB的MongoClient实例-->
    <mongo:mongo-client host="${mongo.host}" port="${mongo.port}">
        <mongo:client-options
                connections-per-host="${mongo.connectionsPerHost}"
                threads-allowed-to-block-for-connection-multiplier="${mongo.threadsAllowedToBlockForConnectionMultiplier}"
                connect-timeout="${mongo.connectTimeout}"
                max-wait-time="${mongo.maxWaitTime}"
                socket-keep-alive="${mongo.socketKeepAlive}"
                socket-timeout="${mongo.socketTimeout}"
        />
    </mongo:mongo-client>

    <!--定义用于连接到数据库的连接工厂-->
    <mongo:db-factory dbname="${mongo.dbname}" mongo-ref="mongoClient"/>

    <!--实际操作mongodb的template,在代码中注入-->
    <bean id="anotherMongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
        <constructor-arg name="mongoDbFactory" ref="mongoDbFactory"/>
    </bean>

</beans>
```

#### 2.3 测试整合

```java
/**
 * @author : heibaiying
 * @description : MongoDB 查询
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:mongodb.xml")
public class MongoDBTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void insert() {
        // 单条插入
        mongoTemplate.insert(new Programmer("xiaoming", 12, 5000.21f, new Date()));
        List<Programmer> programmers = new ArrayList<Programmer>();
        // 批量插入
        programmers.add(new Programmer("xiaohong", 21, 52200.21f, new Date()));
        programmers.add(new Programmer("xiaolan", 34, 500.21f, new Date()));
        mongoTemplate.insert(programmers, Programmer.class);
    }

    // 条件查询
    @Test
    public void select() {
        Criteria criteria = new Criteria();
        criteria.andOperator(where("name").is("xiaohong"), where("age").is(21));
        Query query = new Query(criteria);
        Programmer one = mongoTemplate.findOne(query, Programmer.class);
        System.out.println(one);
    }


    // 更新数据
    @Test
    public void MUpdate() {
        UpdateResult updateResult = mongoTemplate.updateMulti(query(where("name").is("xiaoming")), update("age", 35), Programmer.class);
        System.out.println("更新记录数：" + updateResult.getModifiedCount());
    }

    // 删除指定数据
    @Test
    public void delete() {
        DeleteResult result = mongoTemplate.remove(query(where("name").is("xiaolan")), Programmer.class);
        System.out.println("影响记录数：" + result.getDeletedCount());
        System.out.println("是否成功：" + result.wasAcknowledged());
    }

}
```

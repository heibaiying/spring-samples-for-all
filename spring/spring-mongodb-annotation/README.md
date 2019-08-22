# Spring 整合 MongoDB（注解方式）



<nav>
<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-相关依赖">1.2 相关依赖</a><br/>
<a href="#二整合-MongoDB">二、整合 MongoDB</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-基本配置">2.1 基本配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-整合配置">2.2 整合配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-测试整合">2.3 测试整合</a><br/>
</nav>

## 一、项目说明

### 1.1 项目结构

配置文件位于 com.heibaiying.config 包下，项目以单元测试的方式进行测试。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-mongodb-annotation.png"/> </div>


### 1.2 相关依赖

除了 Spring 的基本依赖外，需要导入 MongoDB 的整合依赖：

```xml
 <!--spring mongodb 整合依赖-->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-mongodb</artifactId>
    <version>2.1.3.RELEASE</version>
</dependency>
```



## 二、整合 MongoDB

### 2.1 基本配置

新建配置文件及其映射类：

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

```java
@Data
@Configuration
@PropertySource(value = "classpath:mongodb.properties")
public class MongoProperty {

    @Value("${mongo.host}")
    private String host;
    @Value("${mongo.port}")
    private int port;
    @Value("${mongo.dbname}")
    private String dbname;
    @Value("${mongo.connectionsPerHost}")
    private int connectionsPerHost;
    @Value("${mongo.threadsAllowedToBlockForConnectionMultiplier}")
    private int multiplier;
    @Value("${mongo.connectTimeout}")
    private int connectTimeout;
    @Value("${mongo.maxWaitTime}")
    private int maxWaitTime;
    @Value("${mongo.socketKeepAlive}")
    private boolean socketKeepAlive;
    @Value("${mongo.socketTimeout}")
    private int socketTimeout;
}
```

### 2.2 整合配置

```java
@Configuration
@ComponentScan(value = "com.heibaiying.*")
public class MongoConfig {

    @Bean
    public MongoDbFactory mongoDbFactory(MongoProperty mongo) {
        MongoClientOptions options = MongoClientOptions.builder()
                .threadsAllowedToBlockForConnectionMultiplier(mongo.getMultiplier())
                .connectionsPerHost(mongo.getConnectionsPerHost())
                .connectTimeout(mongo.getConnectTimeout())
                .maxWaitTime(mongo.getMaxWaitTime())
                .socketTimeout(mongo.getSocketTimeout())
                .build();
        MongoClient client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getPort()), options);
        return new SimpleMongoDbFactory(client, mongo.getDbname());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }
}
```

### 2.3 测试整合

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MongoConfig.class)
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

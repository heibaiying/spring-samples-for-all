# spring boot 整合 mongodb
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-用例结构">1.1 用例结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目主要依赖">1.2 项目主要依赖</a><br/>
<a href="#二整合-mongodb">二、整合 mongodb</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-在applicationyml-中配置mongodb数据源">2.1 在application.yml 中配置mongodb数据源</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22--基于MongoTemplate实现对mongodb的操作">2.2  基于MongoTemplate实现对mongodb的操作</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-使用-data-jpa-方式操作mongodb-推荐使用">2.3 使用 data jpa 方式操作mongodb (推荐使用)</a><br/>
## 正文<br/>




## 一、说明

#### 1.1 用例结构

1. 本用例提供mongdb的简单整合用例；
2. 提供用MongoTemplate的方式操作mongdb，见测试用例MongoOriginalTests.java
3. 提供基于spring data jpa 的方式操作mongodb(推荐)，见测试用例MongoJPATests.java

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-mongodb.png"/> </div>

#### 1.2 项目主要依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```



## 二、整合 mongodb

#### 2.1 在application.yml 中配置mongodb数据源

```yaml
spring:
  data:
    mongodb:
      database: spring
      uri: mongodb://192.168.0.108:27017
```

#### 2.2  基于MongoTemplate实现对mongodb的操作

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoOriginalTests {

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

#### 2.3 使用 data jpa 方式操作mongodb (推荐使用)

1.新建查询结构，查询方法按照支持的关键字命名

```java
public interface ProgrammerRepository extends MongoRepository<Programmer, String> {

    void deleteAllByName(String name);

    Programmer findAllByName(String names);

    Programmer findByNameAndAge(String name, int age);

}
```

2.测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoJPATests {

    @Autowired
    private ProgrammerRepository repository;

    @Test
    public void insert() {
        // 单条插入
        repository.save(new Programmer("python", 23, 21832.34f, new Date()));
        // 批量插入
        List<Programmer> programmers = new ArrayList<Programmer>();
        programmers.add(new Programmer("java", 21, 52200.21f, new Date()));
        programmers.add(new Programmer("Go", 34, 500.21f, new Date()));
        repository.saveAll(programmers);
    }

    // 条件查询
    @Test
    public void select() {
        Programmer java = repository.findByNameAndAge("java", 21);
        Assert.assertEquals(java.getSalary(), 52200.21f, 0.01);
    }


    // 更新数据
    @Test
    public void MUpdate() {
        repository.save(new Programmer("Go", 8, 500.21f, new Date()));
        Programmer go = repository.findAllByName("Go");
        Assert.assertEquals(go.getAge(), 8);
    }

    // 删除指定数据
    @Test
    public void delete() {
        repository.deleteAllByName("python");
        Optional<Programmer> python = repository.findById("python");
        Assert.assertFalse(python.isPresent());
    }

}
```

查询方法支持的关键字如下,更多命名规范可以参见Spring Data MongoDB官方文档[Query Methods](https://docs.spring.io/spring-data/mongodb/docs/2.1.3.RELEASE/reference/html/#mongodb.repositories.queries)：

| Keyword                              | Sample                                                       | Logical result                                               |
| ------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `After`                              | `findByBirthdateAfter(Date date)`                            | `{"birthdate" : {"$gt" : date}}`                             |
| `GreaterThan`                        | `findByAgeGreaterThan(int age)`                              | `{"age" : {"$gt" : age}}`                                    |
| `GreaterThanEqual`                   | `findByAgeGreaterThanEqual(int age)`                         | `{"age" : {"$gte" : age}}`                                   |
| `Before`                             | `findByBirthdateBefore(Date date)`                           | `{"birthdate" : {"$lt" : date}}`                             |
| `LessThan`                           | `findByAgeLessThan(int age)`                                 | `{"age" : {"$lt" : age}}`                                    |
| `LessThanEqual`                      | `findByAgeLessThanEqual(int age)`                            | `{"age" : {"$lte" : age}}`                                   |
| `Between`                            | `findByAgeBetween(int from, int to)`                         | `{"age" : {"$gt" : from, "$lt" : to}}`                       |
| `In`                                 | `findByAgeIn(Collection ages)`                               | `{"age" : {"$in" : [ages…]}}`                                |
| `NotIn`                              | `findByAgeNotIn(Collection ages)`                            | `{"age" : {"$nin" : [ages…]}}`                               |
| `IsNotNull`, `NotNull`               | `findByFirstnameNotNull()`                                   | `{"firstname" : {"$ne" : null}}`                             |
| `IsNull`, `Null`                     | `findByFirstnameNull()`                                      | `{"firstname" : null}`                                       |
| `Like`, `StartingWith`, `EndingWith` | `findByFirstnameLike(String name)`                           | `{"firstname" : name} (name as regex)`                       |
| `NotLike`, `IsNotLike`               | `findByFirstnameNotLike(String name)`                        | `{"firstname" : { "$not" : name }} (name as regex)`          |
| `Containing` on String               | `findByFirstnameContaining(String name)`                     | `{"firstname" : name} (name as regex)`                       |
| `NotContaining` on String            | `findByFirstnameNotContaining(String name)`                  | `{"firstname" : { "$not" : name}} (name as regex)`           |
| `Containing` on Collection           | `findByAddressesContaining(Address address)`                 | `{"addresses" : { "$in" : address}}`                         |
| `NotContaining` on Collection        | `findByAddressesNotContaining(Address address)`              | `{"addresses" : { "$not" : { "$in" : address}}}`             |
| `Regex`                              | `findByFirstnameRegex(String firstname)`                     | `{"firstname" : {"$regex" : firstname }}`                    |
| `(No keyword)`                       | `findByFirstname(String name)`                               | `{"firstname" : name}`                                       |
| `Not`                                | `findByFirstnameNot(String name)`                            | `{"firstname" : {"$ne" : name}}`                             |
| `Near`                               | `findByLocationNear(Point point)`                            | `{"location" : {"$near" : [x,y]}}`                           |
| `Near`                               | `findByLocationNear(Point point, Distance max)`              | `{"location" : {"$near" : [x,y], "$maxDistance" : max}}`     |
| `Near`                               | `findByLocationNear(Point point, Distance min, Distance max)` | `{"location" : {"$near" : [x,y], "$minDistance" : min, "$maxDistance" : max}}` |
| `Within`                             | `findByLocationWithin(Circle circle)`                        | `{"location" : {"$geoWithin" : {"$center" : [ [x, y], distance]}}}` |
| `Within`                             | `findByLocationWithin(Box box)`                              | `{"location" : {"$geoWithin" : {"$box" : [ [x1, y1], x2, y2]}}}` |
| `IsTrue`, `True`                     | `findByActiveIsTrue()`                                       | `{"active" : true}`                                          |
| `IsFalse`, `False`                   | `findByActiveIsFalse()`                                      | `{"active" : false}`                                         |
| `Exists`                             | `findByLocationExists(boolean exists)`                       | `{"location" : {"$exists" : exists }}`                       |


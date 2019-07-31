# spring boot data jpa

## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目主要依赖">1.2 项目主要依赖</a><br/>
<a href="#二data-jpa-的使用">二、data jpa 的使用</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-在applicationyml-中配置数据源">2.1 在application.yml 中配置数据源</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-新建查询接口">2.2 新建查询接口</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23--测试类">2.3  测试类</a><br/>
## 正文<br/>




## 一、说明

#### 1.1 项目结构

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-data-jpa.png"/> </div>

#### 1.2 项目主要依赖

```xml
<dependencies>
   <!-- data-jpa -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!--引入 mysql 驱动-->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.13</version>
    </dependency>
     <!--单元测试包-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 二、data jpa 的使用

#### 2.1 在application.yml 中配置数据源

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/mysql?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    #Hibernate 默认创建的表是 myisam 引擎，可以用以下方式指定为使用 innodb 创建表
    database-platform: org.hibernate.dialect.MySQL57Dialect
    show-sql: true
```

#### 2.2 新建查询接口

```java
/**
 * @author : heibaiying
 * @description : 查询接口继承自 CrudRepository,CrudRepository 默认定义了部分增删改查方法
 */
public interface ProgRepository extends CrudRepository<Programmer, Integer> {

    /*
     * 方法名遵循命名规范的查询 更多命名规范可以参考官方文档所列出的这张表格
     */
    List<Programmer> findAllByName(String name);

    /*
     *分页排序查询
     */
    Page<Programmer> findAll(Pageable pageable);


    /*
     * 占位符查询
     */
    @Query(value = "select u from Programmer u where u.name = ?1 or u.salary =  ?2")
    List<Programmer> findByConditionAndOrder(String name, float salary, Sort.Order order);


    /*
     * 传入参数名称
     */
    @Query("select u from Programmer u where u.name = :name or u.age = :age")
    Programmer findByParam(@Param("name") String name,
                           @Param("age") int age);
}

```

关于查询方法遵循的命名规范和关键词见下表:

| Keyword             | Sample                                                       | JPQL snippet                                                 |
| ------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `And`               | `findByLastnameAndFirstname`                                 | `… where x.lastname = ?1 and x.firstname = ?2`               |
| `Or`                | `findByLastnameOrFirstname`                                  | `… where x.lastname = ?1 or x.firstname = ?2`                |
| `Is,Equals`         | `findByFirstname`,`findByFirstnameIs`,<br>`findByFirstnameEquals` | `… where x.firstname = ?1`                                   |
| `Between`           | `findByStartDateBetween`                                     | `… where x.startDate between ?1 and ?2`                      |
| `LessThan`          | `findByAgeLessThan`                                          | `… where x.age < ?1`                                         |
| `LessThanEqual`     | `findByAgeLessThanEqual`                                     | `… where x.age <= ?1`                                        |
| `GreaterThan`       | `findByAgeGreaterThan`                                       | `… where x.age > ?1`                                         |
| `GreaterThanEqual`  | `findByAgeGreaterThanEqual`                                  | `… where x.age >= ?1`                                        |
| `After`             | `findByStartDateAfter`                                       | `… where x.startDate > ?1`                                   |
| `Before`            | `findByStartDateBefore`                                      | `… where x.startDate < ?1`                                   |
| `IsNull`            | `findByAgeIsNull`                                            | `… where x.age is null`                                      |
| `IsNotNull,NotNull` | `findByAge(Is)NotNull`                                       | `… where x.age not null`                                     |
| `Like`              | `findByFirstnameLike`                                        | `… where x.firstname like ?1`                                |
| `NotLike`           | `findByFirstnameNotLike`                                     | `… where x.firstname not like ?1`                            |
| `StartingWith`      | `findByFirstnameStartingWith`                                | `… where x.firstname like ?1`(parameter bound with appended `%`) |
| `EndingWith`        | `findByFirstnameEndingWith`                                  | `… where x.firstname like ?1`(parameter bound with prepended `%`) |
| `Containing`        | `findByFirstnameContaining`                                  | `… where x.firstname like ?1`(parameter bound wrapped in `%`) |
| `OrderBy`           | `findByAgeOrderByLastnameDesc`                               | `… where x.age = ?1 order by x.lastname desc`                |
| `Not`               | `findByLastnameNot`                                          | `… where x.lastname <> ?1`                                   |
| `In`                | `findByAgeIn(Collection<Age> ages)`                          | `… where x.age in ?1`                                        |
| `NotIn`             | `findByAgeNotIn(Collection<Age> ages)`                       | `… where x.age not in ?1`                                    |
| `True`              | `findByActiveTrue()`                                         | `… where x.active = true`                                    |
| `False`             | `findByActiveFalse()`                                        | `… where x.active = false`                                   |
| `IgnoreCase`        | `findByFirstnameIgnoreCase`                                  | `… where UPPER(x.firstame) = UPPER(?1)`                      |

#### 2.3  测试类

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataJPATests {

    @Autowired
    private ProgRepository repository;

    /**
     * 保存数据测试
     */
    @Test
    public void save() {
        // 保存单条数据
        repository.save(new Programmer("pro01", 12, 2121.34f, new Date()));
        // 保存多条数据
        List<Programmer> programmers = new ArrayList<>();
        programmers.add(new Programmer("pro02", 22, 3221.34f, new Date()));
        programmers.add(new Programmer("pro03", 32, 3321.34f, new Date()));
        programmers.add(new Programmer("pro04", 44, 4561.34f, new Date()));
        programmers.add(new Programmer("pro01", 44, 4561.34f, new Date()));
        repository.saveAll(programmers);
    }


    /**
     * 查询数据测试
     */
    @Test
    public void get() {

        // 遵循命名规范的查询
        List<Programmer> programmers = repository.findAllByName("pro01");
        programmers.forEach(System.out::println);

        // 传入参数名称
        Programmer param = repository.findByParam("pro02", 22);
        System.out.println("findByParam:" + param);

        // 占位符查询
        List<Programmer> byCondition = repository.findByConditionAndOrder("pro03", 3321.34f, Sort.Order.asc("salary"));
        System.out.println("byCondition:" + byCondition);

        //条件与分页查询 需要注意的是这里的页数是从第 0 页开始计算的
        Page<Programmer> page = repository.findAll(PageRequest.of(0, 10, Sort.Direction.DESC, "salary"));
        page.get().forEach(System.out::println);
    }


    /**
     * 更新数据测试
     */
    @Test
    public void update() {
        // 保存主键相同的数据就认为是更新操作
        repository.save(new Programmer(1, "updatePro01", 12, 2121.34f, new Date()));
        Optional<Programmer> programmer = repository.findById(1);
        Assert.assertEquals(programmer.get().getName(), "updatePro01");
    }

    /**
     * 删除数据测试
     */
    @Test
    public void delete() {
        Optional<Programmer> programmer = repository.findById(2);
        if (programmer.isPresent()) {
            repository.deleteById(2);
        }
        Assert.assertFalse(programmer.isPresent());
    }
}
```


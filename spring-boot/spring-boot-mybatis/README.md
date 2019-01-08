# spring boot 整合 mybatis

## 一、说明

#### 1.1 项目结构

1. 项目查询用的表对应的建表语句放置在resources的sql文件夹下；

2. 关于mybatis sql的写法提供两种方式:

   xml 写法：对应的类为ProgrammerMapper.java 和 programmerMapper.xml，用MybatisXmlTest进行测试；

   注解写法：对应的类为Programmer.java ，用MybatisAnnotationTest进行测试。

![spring-boot-servlet](D:\spring-samples-for-all\pictures\spring-boot-mybatis.png)

#### 1.2 项目主要依赖

需要说明的是按照spring 官方对应自定义的starter 命名规范的推荐：

- 官方的starter命名：spring-boot-starter-XXXX
- 其他第三方starter命名：XXXX-spring-boot-starte

所以mybatis的starter命名为mybatis-spring-boot-starter，如果有自定义starter需求，也需要按照此命名规则进行命名。

```xml
<!--spring 1.5 x 以上版本对应 mybatis 1.3.x (1.3.1)
        关于更多spring-boot 与 mybatis 的版本对应可以参见 <a href="http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/">-->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.2</version>
</dependency>
<!--引入mysql驱动-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.13</version>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

spring boot 与 mybatis 版本的对应关系：

| MyBatis-Spring-Boot-Starter | [MyBatis-Spring](http://www.mybatis.org/spring/index.html#Requirements) | Spring Boot   |
| --------------------------- | ------------------------------------------------------------ | ------------- |
| **1.3.x (1.3.1)**           | 1.3 or higher                                                | 1.5 or higher |
| **1.2.x (1.2.1)**           | 1.3 or higher                                                | 1.4 or higher |
| **1.1.x (1.1.1)**           | 1.3 or higher                                                | 1.3 or higher |
| **1.0.x (1.0.2)**           | 1.2 or higher                                                | 1.3 or higher |

## 二、整合 mybatis

#### 2.1 在application.yml 中配置数据源

spring boot 2.x 版本默认采用Hikari作为数据库连接池，Hikari是目前java平台性能最好的连接池，性能好于druid。

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/mysql?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

    # 如果不想配置对数据库连接池做特殊配置的话,以下关于连接池的配置就不是必须的
    # spring-boot 2 默认采用高性能的 Hikari 作为连接池 更多配置可以参考 https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      # 池中维护的最小空闲连接数
      minimum-idle: 10
      # 池中最大连接数，包括闲置和使用中的连接
      maximum-pool-size: 20
      # 此属性控制从池返回的连接的默认自动提交行为。默认为true
      auto-commit: true
      # 允许最长空闲时间
      idle-timeout: 30000
      # 此属性表示连接池的用户定义名称，主要显示在日志记录和JMX管理控制台中，以标识池和池配置。 默认值：自动生成
      pool-name: custom-hikari
      #此属性控制池中连接的最长生命周期，值0表示无限生命周期，默认1800000即30分钟
      max-lifetime: 1800000
      # 数据库连接超时时间,默认30秒，即30000
      connection-timeout: 30000
      # 连接测试sql 这个地方需要根据数据库方言差异而配置 例如 oracle 就应该写成  select 1 from dual
      connection-test-query: SELECT 1

# mybatis 相关配置
mybatis:
    # 指定 sql xml 文件的位置
    mapper-locations: classpath*:mappers/*.xml
    configuration:
      # 当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。
      # oracle数据库建议配置为JdbcType.NULL, 默认是Other
      jdbc-type-for-null: 'null'
      # 是否打印sql语句 调试的时候可以开启
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

#### 2.2  xml方式的sql语句

新建 ProgrammerMapper.java 和 programmerMapper.xml，及其测试类

```java
@Mapper
public interface ProgrammerMapper {

    void save(Programmer programmer);

    Programmer selectById(int id);

    int modify(Programmer programmer);

    void delete(int id);
}
```

```xml
<?xml version="1.0"  encoding="utf-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.heibaiying.springboot.dao.ProgrammerMapper">

    <insert id="save">
        insert into programmer (name, age, salary, birthday) VALUES (#{name}, #{age}, #{salary}, #{birthday})
    </insert>

    <select id="selectById" resultType="com.heibaiying.springboot.bean.Programmer">
      select * from programmer where name = #{id}
    </select>

    <update id="modify">
        update programmer set name=#{name},age=#{age},salary=#{salary},birthday=#{birthday} where id=#{id}
    </update>

    <delete id="delete">
        delete from programmer where id = #{id}
    </delete>

</mapper>
```

测试类

```java
/***
 * @description: xml Sql测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisXmlTest {

    @Autowired
    private ProgrammerMapper mapper;

    @Test
    public void save() {
        mapper.save(new Programmer("xiaominng", 12, 3467.34f, new Date()));
        mapper.save(new Programmer("xiaominng", 12, 3467.34f, new Date()));
    }

    @Test
    public void modify() {
        mapper.modify(new Programmer(1, "xiaohong", 112, 347.34f, new Date()));
    }

    @Test
    public void selectByCondition() {
        Programmer programmers = mapper.selectById(1);
        System.out.println(programmers);
    }

    @Test
    public void delete() {
        mapper.delete(2);
        Programmer programmers = mapper.selectById(2);
        Assert.assertNull(programmers);
    }
}
```

#### 2.3 注解方式的sql语句

```java
@Mapper
public interface ProgrammerDao {

    @Insert("insert into programmer (name, age, salary, birthday) VALUES (#{name}, #{age}, #{salary}, #{birthday})")
    void save(Programmer programmer);

    @Select("select * from programmer where name = #{id}")
    Programmer selectById(int id);

    @Update("update programmer set name=#{name},age=#{age},salary=#{salary},birthday=#{birthday} where id=#{id}")
    int modify(Programmer programmer);

    @Delete(" delete from programmer where id = #{id}")
    void delete(int id);
}
```

测试类

```java
/***
 * @description: 注解Sql测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisAnnotationTest {

    @Autowired
    private ProgrammerDao programmerDao;

    @Test
    public void save() {
        programmerDao.save(new Programmer("xiaominng", 12, 3467.34f, new Date()));
        programmerDao.save(new Programmer("xiaominng", 12, 3467.34f, new Date()));
    }

    @Test
    public void modify() {
        programmerDao.modify(new Programmer(1, "xiaolan", 21, 347.34f, new Date()));
    }

    @Test
    public void selectByCondition() {
        Programmer programmers = programmerDao.selectById(1);
        System.out.println(programmers);
    }

    @Test
    public void delete() {
        programmerDao.delete(3);
        Programmer programmers = programmerDao.selectById(3);
        Assert.assertNull(programmers);
    }
}

```


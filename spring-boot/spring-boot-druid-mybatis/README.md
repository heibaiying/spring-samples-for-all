# Spring Boot 整合 Druid+Mybatis




<nav>
<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-基本依赖">1.2 基本依赖</a><br/>
<a href="#二整合-Druid-+-Mybatis">二、整合 Druid + Mybatis</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-配置数据源">2.1 配置数据源</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22--整合查询">2.2  整合查询</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-监控数据">2.3 监控数据</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-Druid-控制台">2.4 Druid 控制台</a><br/>
</nav>

## 一、项目说明

### 1.1 项目结构

1. 项目涉及表的建表语句放置在 resources 的 sql 文件夹下；

2. 为了演示 Druid 控制台的功能，项目以 Web 的方式构建。

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-boot-druid-mybatis.png"/> </div>

### 1.2 基本依赖

按照 Spring 官方对于自定义的 starter 命名规范的要求：

- 官方的 starter 命名：spring-boot-starter-XXXX
- 其他第三方 starter 命名：XXXX-spring-boot-starter 

所以 Mybatis 的 starter 命名为 mybatis-spring-boot-starter，如果有自定义 starter 需求，也需要按照此命名规则进行命名。

```xml
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
<!--druid 依赖-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```

Spring Boot 与 Mybatis 版本的对应关系：

| MyBatis-Spring-Boot-Starter | [MyBatis-Spring](http://www.mybatis.org/spring/index.html#Requirements) | Spring Boot   |
| --------------------------- | ------------------------------------------------------------ | ------------- |
| **1.3.x (1.3.1)**           | 1.3 or higher                                                | 1.5 or higher |
| **1.2.x (1.2.1)**           | 1.3 or higher                                                | 1.4 or higher |
| **1.1.x (1.1.1)**           | 1.3 or higher                                                | 1.3 or higher |
| **1.0.x (1.0.2)**           | 1.2 or higher                                                | 1.3 or higher |



## 二、整合 Druid + Mybatis

### 2.1 配置数据源

本用例采用 Druid 作为数据库连接池，虽然 Druid 性能略逊于 Hikari，但提供了更为全面的监控管理，可以按照实际需求选用 Druid 或者 Hikari。（关于 Hikari 数据源的配置可以参考 [spring-boot-mybatis 项目](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-mybatis)）

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/mysql?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

    # 使用 druid 作为连接池  更多配置的说明可以参见 druid starter 中文文档 https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      # 初始化时建立物理连接的个数。初始化发生在显示调用 init 方法，或者第一次 getConnection 时
      initialSize: 5
      # 最小连接池数量
      minIdle: 5
      # 最大连接池数量
      maxActive: 10
      # 获取连接时最大等待时间，单位毫秒。配置了 maxWait 之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置 useUnfairLock 属性为 true 使用非公平锁。
      maxWait: 60000
      # Destroy 线程会检测连接的间隔时间，如果连接空闲时间大于等于 minEvictableIdleTimeMillis 则关闭物理连接。
      timeBetweenEvictionRunsMillis: 60000
      # 连接保持空闲而不被驱逐的最小时间
      minEvictableIdleTimeMillis: 300000
      # 用来检测连接是否有效的 sql 因数据库方言而差, 例如 oracle 应该写成 SELECT 1 FROM DUAL
      validationQuery: SELECT 1
      # 建议配置为 true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于 timeBetweenEvictionRunsMillis，执行 validationQuery 检测连接是否有效。
      testWhileIdle: true
      # 申请连接时执行 validationQuery 检测连接是否有效，做了这个配置会降低性能。
      testOnBorrow: false
      # 归还连接时执行 validationQuery 检测连接是否有效，做了这个配置会降低性能。
      testOnReturn: false
      # 是否自动回收超时连接
      removeAbandoned: true
      # 超时时间 (以秒数为单位)
      remove-abandoned-timeout: 180

      # druid 监控的配置 如果不使用 druid 的监控功能的话 以下配置就不是必须的
      # 本项目监控台访问地址: http://localhost:8080/druid/login.html

      # WebStatFilter 用于采集 web-jdbc 关联监控的数据。
      # 更多配置可参见: https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_%E9%85%8D%E7%BD%AEWebStatFilter
      web-stat-filter:
        # 是否开启 WebStatFilter 默认是 true
        enabled: true
        # 需要拦截的 url
        url-pattern: /*
        # 排除静态资源的请求
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"

      # Druid 内置提供了一个 StatViewServlet 用于展示 Druid 的统计信息。
      # 更多配置可参见:https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_StatViewServlet%E9%85%8D%E7%BD%AE
      stat-view-servlet:
        #是否启用 StatViewServlet 默认值 true
        enabled: true
        # 需要拦截的 url
        url-pattern: /druid/*
        # 允许清空统计数据
        reset-enable: true
        login-username: druid
        login-password: druid



# mybatis 相关配置
mybatis:
    configuration:
      # 当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。
      # oracle 数据库建议配置为 JdbcType.NULL, 默认是 Other
      jdbc-type-for-null: 'null'
      # 是否打印 sql 语句 调试的时候可以开启
log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 2.2  整合查询

新建查询接口和测试 Controller：

```java
@Mapper
public interface ProgrammerDao {


    @Select("select * from programmer")
    List<Programmer> selectAll();

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

```xml
@RestController
public class ProgrammerController {

    @Autowired
    private ProgrammerDao programmerDao;

    @GetMapping("/programmers")
    public List<Programmer> get() {
        return programmerDao.selectAll();
    }
}
```

### 2.3 监控数据

在 Spring Boot 中可以通过 HTTP 接口将 Druid 的监控数据以  JSON 的形式暴露出去，可以用于健康检查等功能：

```java
@RestController
public class DruidStatController {

    @GetMapping("/stat")
    public Object druidStat() {
        // DruidStatManagerFacade#getDataSourceStatDataList 该方法可以获取所有数据源的监控数据
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
```

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/druid-status.png"/> </div>

### 2.4 Druid 控制台

默认访问地址为 http://localhost:8080/druid/login.html ：

<div align="center"> <img src="https://gitee.com/heibaiying/spring-samples-for-all/raw/master/pictures/spring-boot-druid%20%E6%8E%A7%E5%88%B6%E5%8F%B0.png"/> </div>

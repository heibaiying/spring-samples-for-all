# spring boot 整合 druid+mybatis
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目主要依赖">1.2 项目主要依赖</a><br/>
<a href="#二整合-druid-+-mybatis">二、整合 druid + mybatis</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-在applicationyml-中配置数据源">2.1 在application.yml 中配置数据源</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22--新建查询接口和controller">2.2  新建查询接口和controller</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-关于druid监控数据的外部化调用">2.3 关于druid监控数据的外部化调用</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-druid-控制台的使用默认访问地址-http//localhost8080/druid/loginhtml">2.4 druid 控制台的使用，默认访问地址 http://localhost:8080/druid/login.html</a><br/>
## 正文<br/>




## 一、说明

#### 1.1 项目结构

1. 项目查询用的表对应的建表语句放置在resources的sql文件夹下；

2. 为了使用druid控制台的功能，项目以web的方式构建。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-druid-mybatis.png"/> </div>

#### 1.2 项目主要依赖

需要说明的是按照spring 官方对于自定义的starter 命名规范的推荐：

- 官方的starter命名：spring-boot-starter-XXXX
- 其他第三方starter命名：XXXX-spring-boot-starte

所以mybatis的starter命名为mybatis-spring-boot-starter，如果有自定义starter需求，也需要按照此命名规则进行命名。

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

spring boot 与 mybatis 版本的对应关系：

| MyBatis-Spring-Boot-Starter | [MyBatis-Spring](http://www.mybatis.org/spring/index.html#Requirements) | Spring Boot   |
| --------------------------- | ------------------------------------------------------------ | ------------- |
| **1.3.x (1.3.1)**           | 1.3 or higher                                                | 1.5 or higher |
| **1.2.x (1.2.1)**           | 1.3 or higher                                                | 1.4 or higher |
| **1.1.x (1.1.1)**           | 1.3 or higher                                                | 1.3 or higher |
| **1.0.x (1.0.2)**           | 1.2 or higher                                                | 1.3 or higher |



## 二、整合 druid + mybatis

#### 2.1 在application.yml 中配置数据源

本用例采用druid作为数据库连接池，虽然druid性能略逊于Hikari，但是提供了更为全面的监控管理，可以按照实际需求选用druid或者Hikari。（关于Hikari数据源的配置可以参考[spring-boot-mybatis项目](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-mybatis)）

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
      # 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
      initialSize: 5
      # 最小连接池数量
      minIdle: 5
      # 最大连接池数量
      maxActive: 10
      # 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
      maxWait: 60000
      # Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。
      timeBetweenEvictionRunsMillis: 60000
      # 连接保持空闲而不被驱逐的最小时间
      minEvictableIdleTimeMillis: 300000
      # 用来检测连接是否有效的sql 因数据库方言而差, 例如 oracle 应该写成 SELECT 1 FROM DUAL
      validationQuery: SELECT 1
      # 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
      testWhileIdle: true
      # 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
      testOnBorrow: false
      # 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
      testOnReturn: false
      # 是否自动回收超时连接
      removeAbandoned: true
      # 超时时间(以秒数为单位)
      remove-abandoned-timeout: 180

      # druid 监控的配置 如果不使用 druid 的监控功能的话 以下配置就不是必须的
      # 本项目监控台访问地址: http://localhost:8080/druid/login.html

      # WebStatFilter用于采集web-jdbc关联监控的数据。
      # 更多配置可参见: https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_%E9%85%8D%E7%BD%AEWebStatFilter
      web-stat-filter:
        # 是否开启 WebStatFilter 默认是true
        enabled: true
        # 需要拦截的url
        url-pattern: /*
        # 排除静态资源的请求
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"

      # Druid内置提供了一个StatViewServlet用于展示Druid的统计信息。
      # 更多配置可参见:https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_StatViewServlet%E9%85%8D%E7%BD%AE
      stat-view-servlet:
        #是否启用StatViewServlet 默认值true
        enabled: true
        # 需要拦截的url
        url-pattern: /druid/*
        # 允许清空统计数据
        reset-enable: true
        login-username: druid
        login-password: druid



# mybatis 相关配置
mybatis:
    configuration:
      # 当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。
      # oracle数据库建议配置为JdbcType.NULL, 默认是Other
      jdbc-type-for-null: 'null'
      # 是否打印sql语句 调试的时候可以开启
log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

#### 2.2  新建查询接口和controller

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

#### 2.3 关于druid监控数据的外部化调用

```java
/**
 * @author : heibaiying
 * @description :在 Spring Boot 中可以通过 HTTP 接口将 Druid 监控数据以JSON 的形式暴露出去，
 * 实际使用中你可以根据你的需要自由地对监控数据、暴露方式进行扩展。
 */

@RestController
public class DruidStatController {

    @GetMapping("/stat")
    public Object druidStat() {
        // DruidStatManagerFacade#getDataSourceStatDataList 该方法可以获取所有数据源的监控数据
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
```

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/druid-status.png"/> </div>



#### 2.4 druid 控制台的使用，默认访问地址 http://localhost:8080/druid/login.html

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-druid%20%E6%8E%A7%E5%88%B6%E5%8F%B0.png"/> </div>

# spring 整合 mybatis（xml配置方式）
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构">1.1 项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目依赖">1.2 项目依赖</a><br/>
<a href="#二spring-整合-mybatis">二、spring 整合 mybatis</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21--在resources文件夹下新建数据库配置文件jdbcproperties">2.1  在resources文件夹下新建数据库配置文件jdbc.properties</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22--配置数据源和mybatis会话工厂定义事务管理器">2.2  配置数据源和mybatis会话工厂、定义事务管理器</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-新建mybtais配置文件按照需求配置额外参数-更多settings配置项可以参考[官方文档]http//wwwmybatisorg/mybatis-3/zh/configurationhtml">2.3 新建mybtais配置文件，按照需求配置额外参数， 更多settings配置项可以参考[官方文档](http://www.mybatis.org/mybatis-3/zh/configuration.html)</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-新建查询接口及其对应的mapper文件">2.4 新建查询接口及其对应的mapper文件</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#25-新建测试类进行测试">2.5 新建测试类进行测试</a><br/>
## 正文<br/>


## 一、说明

#### 1.1 项目结构

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-mybatis.png"/> </div>

#### 1.2 项目依赖

除了spring相关依赖外，还需要导入数据库驱动和对应的mybatis依赖包

```xml
<!--jdbc 相关依赖包-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${spring-base-version}</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.13</version>
</dependency>
<dependency>
    <groupId>com.oracle</groupId>
    <artifactId>ojdbc6</artifactId>
    <version>11.2.0.3.0</version>
</dependency>
<!--mybatis 依赖包-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>1.3.2</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.6</version>
</dependency>
```

## 二、spring 整合 mybatis

#### 2.1  在resources文件夹下新建数据库配置文件jdbc.properties

```properties
# mysql 数据库配置
mysql.driverClassName=com.mysql.jdbc.Driver
mysql.url=jdbc:mysql://localhost:3306/mysql
mysql.username=root
mysql.password=root

# oracle 数据库配置
oracle.driverClassName=oracle.jdbc.driver.OracleDriver
oracle.url=jdbc:oracle:thin:@//IP地址:端口号/数据库实例名
oracle.username=用户名
oracle.password=密码
```

#### 2.2  配置数据源和mybatis会话工厂、定义事务管理器

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 开启注解包扫描-->
    <context:component-scan base-package="com.heibaiying.*"/>

    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--配置数据源-->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <!--Mysql 配置-->
        <property name="driverClassName" value="${mysql.driverClassName}"/>
        <property name="url" value="${mysql.url}"/>
        <property name="username" value="${mysql.username}"/>
        <property name="password" value="${mysql.password}"/>
        <!--Oracle 配置-->
        <!--<property name="driverClassName" value="${oracle.driverClassName}"/>
        <property name="url" value="${oracle.url}"/>
        <property name="username" value="${oracle.username}"/>
        <property name="password" value="${oracle.password}"/>-->
    </bean>

    <!--配置 mybatis 会话工厂 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!--指定mapper文件所在的位置-->
        <property name="mapperLocations" value="classpath*:/mappers/**/*.xml"/>
        <property name="configLocation" value="classpath:mybatisConfig.xml"/>
    </bean>

    <!--扫描注册接口 -->
    <!--作用:从接口的基础包开始递归搜索，并将它们注册为 MapperFactoryBean(只有至少一种方法的接口才会被注册;, 具体类将被忽略)-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--指定会话工厂 -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <!-- 指定mybatis接口所在的包 -->
        <property name="basePackage" value="com.heibaiying.dao"/>
    </bean>


    <!--定义事务管理器-->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 开启事务注解@Transactional支持 -->
    <tx:annotation-driven/>

</beans>
```

#### 2.3 新建mybtais配置文件，按照需求配置额外参数， 更多settings配置项可以参考[官方文档](http://www.mybatis.org/mybatis-3/zh/configuration.html)

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!-- mybatis 配置文件 -->
<configuration>
    <settings>
        <!-- 开启驼峰命名 -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- 打印查询sql -->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>

</configuration>

<!--更多settings配置项可以参考官方文档: <a href="http://www.mybatis.org/mybatis-3/zh/configuration.html"/>-->

```

#### 2.4 新建查询接口及其对应的mapper文件

```java
public interface MysqlDao {

    List<Relation> get();
}
```

```xml
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.heibaiying.dao.MysqlDao">

    <select id="queryById" resultType="com.heibaiying.bean.Relation">
        SELECT help_keyword_id AS id,name
        FROM HELP_KEYWORD
        WHERE HELP_KEYWORD_ID = #{id}
    </select>

</mapper>
```

```mysql
public interface OracleDao {

    List<Flow> queryById(long id);
}

```

```xml
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.heibaiying.dao.OracleDao">

    <select id="queryById" resultType="com.heibaiying.bean.Flow">
        select * from APEX_030200.WWV_FLOW_CALS where ID = #{id}
    </select>

</mapper>
```

#### 2.5 新建测试类进行测试

```java
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
public class MysqlDaoTest {

    @Autowired
    private MysqlDao mysqlDao;

    @Test
    public void get() {
        List<Relation> relations = mysqlDao.get();
        if (relations != null) {
            for (Relation relation : relations) {
                System.out.println(relation.getId() + " " + relation.getName());
            }
        }
    }
}

```

```java
@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
public class OracleDaoTest {

    /*注入接口时: 如果接口有多个实现类 可以用这个注解指定具体的实现类*/
    @Qualifier("oracleDaoImpl")
    @Autowired
    private OracleDao oracleDao;

    @Test
    public void get() {
        List<Flow> flows = oracleDao.get();
        if (flows != null) {
            for (Flow flow : flows) {
                System.out.println(flow.getId() + " " + flow.getPlugId());
            }
        }
    }
}

```


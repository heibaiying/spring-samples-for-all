# Spring 整合 JDBC Template（XML 配置方式）


<nav>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11--项目结构">1.1  项目结构</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12--项目依赖">1.2  项目依赖</a><br/>
<a href="#二整合-JDBC-Template">二、整合 JDBC Template</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-数据库配置">2.1 数据库配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-配置数据源">2.2 配置数据源</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-数据查询">2.3 数据查询</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-测试查询">2.4 测试查询</a><br/>
</nav>

## 一、说明

#### 1.1  项目结构

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-jdbc.png"/> </div>
#### 1.2  项目依赖

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
</dependencies>
```



## 二、整合 JDBC Template

#### 2.1 数据库配置

在 resources 文件夹下新建数据库配置文件 `jdbc.properties`，内容如下：

```properties
# mysql 数据库配置
mysql.driverClassName=com.mysql.jdbc.Driver
mysql.url=jdbc:mysql://localhost:3306/mysql
mysql.username=root
mysql.password=root

# oracle 数据库配置
oracle.driverClassName=oracle.jdbc.driver.OracleDriver
oracle.url=jdbc:oracle:thin:@//IP 地址:端口号/数据库实例名
oracle.username=用户名
oracle.password=密码
```

#### 2.2 配置数据源

在配置文件中配置 JDBC 数据源并定义事务管理器：

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

    <!--配置 jdbcTemplate -->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <!--引用上面的数据源-->
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--定义事务管理器-->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 开启事务注解@Transactional 支持 -->
    <tx:annotation-driven/>

</beans>
```

#### 2.3 数据查询

新建查询接口及其实现类，以下示例分别查询的是 MySQL 和 Oracle 中的字典表：

```java
@Repository
public class MysqlDaoImpl implements MysqlDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 更多 JDBC 的使用可以参考官方文档
     * @see <a href="https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/data-access.html#jdbc-JdbcTemplate">JdbcTemplate</a>
     */
    public List<Relation> get() {
        List<Relation> relations = jdbcTemplate.query("select * from help_keyword where help_keyword_id = ? ", new Object[]{691},
                new RowMapper<Relation>() {
                    public Relation mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Relation relation = new Relation();
                        relation.setId(rs.getString("help_keyword_id"));
                        relation.setName(rs.getString("name"));
                        return relation;
                    }

                });
        return relations;
    }
}

```

```java
@Repository
public class OracleDaoImpl implements OracleDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Flow> get() {
        List<Flow> flows = jdbcTemplate.query("select * from APEX_030200.WWV_FLOW_CALS where ID = ? ", new Object[]{217584603977429772L},
                new RowMapper<Flow>() {
                    public Flow mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Flow flow = new Flow();
                        flow.setId(rs.getLong("ID"));
                        flow.setFlowId(rs.getLong("FLOW_ID"));
                        flow.setPlugId(rs.getLong("PLUG_ID"));
                        return flow;
                    }

                });
        return flows;
    }
}
```

#### 2.4 测试查询

新建测试类进行测试：

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


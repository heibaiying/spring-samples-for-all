# spring 整合 jdbc template（注解方式）

## 目录<br/>
<a href="#1说明">1.说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12--项目依赖">1.2  项目依赖</a><br/>
<a href="#二spring-整合-jdbc-template">二、spring 整合 jdbc template</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-在resources文件夹下新建数据库配置文件mysqlpropertiesoracleproperties及其映射类">2.1 在resources文件夹下新建数据库配置文件mysql.properties、oracle.properties及其映射类</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-新建数据库配置类DatabaseConfigjava">2.2 新建数据库配置类DatabaseConfig.java</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-新建查询接口及其实现类这里我查询的表是mysql和oracle中的字典表">2.3 新建查询接口及其实现类</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-新建测试类进行测试">2.4 新建测试类进行测试</a><br/>
## 正文<br/>


## 1.说明

#### 1.1 项目目录结构

1. 数据源配置位于 config 目录下的 DatabaseConfig.java 和 DataSourceConfig.java
2. 项目以单元测试的方法进行测试

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-jdbc-annotation.png"/> </div>



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

## 二、spring 整合 jdbc template

#### 2.1 在resources文件夹下新建数据库配置文件mysql.properties、oracle.properties及其映射类

```properties
# mysql 数据库配置
mysql.driverClassName=com.mysql.jdbc.Driver
mysql.url=jdbc:mysql://localhost:3306/mysql
mysql.username=root
mysql.password=root
```

```properties
# oracle 数据库配置
oracle.driverClassName=oracle.jdbc.driver.OracleDriver
oracle.url=jdbc:oracle:thin:@//IP 地址:端口号/数据库实例名
oracle.username=用户名
oracle.password=密码
```

```java
@Configuration
@PropertySource(value = "classpath:mysql.properties")
@Data
public class DataSourceConfig {

    @Value("${mysql.driverClassName}")
    private String driverClassName;
    @Value("${mysql.url}")
    private String url;
    @Value("${mysql.username}")
    private String username;
    @Value("${mysql.password}")
    private String password;

}

```

#### 2.2 新建数据库配置类DatabaseConfig.java

```java
@Configuration
@EnableTransactionManagement // 开启声明式事务处理 等价于 xml 中<tx:annotation-driven/>
@ComponentScan(basePackages = {"com.heibaiying.*"})
public class DatabaseConfig {

    /**
     * 配置数据源
     */
    @Bean
    public DriverManagerDataSource dataSource(DataSourceConfig sourceConfig) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(sourceConfig.getDriverClassName());
        dataSource.setUrl(sourceConfig.getUrl());
        dataSource.setUsername(sourceConfig.getUsername());
        dataSource.setPassword(sourceConfig.getPassword());
        return dataSource;
    }


    /**
     * 配置 jdbcTemplate
     * @param dataSource 这个参数的名称需要保持和上面方法名一致 才能自动注入,因为
     *                   采用@Bean 注解生成的 bean 默认采用方法名为名称，当然也可以在使用@Bean 时指定 name 属性
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DriverManagerDataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    /**
     * 定义事务管理器
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DriverManagerDataSource dataSource) {
        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(dataSource);
        return manager;
    }

}

```

#### 2.3 新建查询接口及其实现类,这里我查询的表是mysql和oracle中的字典表

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

    /**
     * 更多 JDBC 的使用可以参考官方文档
     * @see <a href="https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/data-access.html#jdbc-JdbcTemplate">JdbcTemplate</a>
     */
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

#### 2.4 新建测试类进行测试

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ServletConfig.class})
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
@ContextConfiguration(classes = {ServletConfig.class})
public class OracleDaoTest {

    /*注入接口时: 如果接口有多个实现类 可以用这个指定具体的实现类*/
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


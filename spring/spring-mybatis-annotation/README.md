# spring 整合 mybatis（注解方式）

## 一、说明

#### 1.1 项目结构

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-mybatis-annotation.png"/> </div>

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

#### 2.1  在resources文件夹下新建数据库配置文件jdbc.properties及其映射类

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

#### 2.2  配置数据源和mybatis会话工厂、定义事务管理器

```java
/**
 * @author : heibaiying
 */
@Configuration
@EnableTransactionManagement // 开启声明式事务处理 等价于xml中<tx:annotation-driven/>
@ComponentScan(basePackages = {"com.heibaiying.*"})
public class DatabaseConfig {

    /* @Autowired
     * private DataSourceConfig sourceConfig;
     * 不要采用这种方式注入DataSourceConfig,由于类的加载顺序影响会报空指针异常
     * 最好的方式是在DriverManagerDataSource构造中采用参数注入
     */

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
     * 配置mybatis 会话工厂
     *
     * @param dataSource 这个参数的名称需要保持和上面方法名一致 才能自动注入,因为
     *                   采用@Bean注解生成的bean 默认采用方法名为名称，当然也可以在使用@Bean时指定name属性
     */
    @Bean
    public SqlSessionFactoryBean sessionFactoryBean(DriverManagerDataSource dataSource) throws IOException {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:/mappers/**/*.xml"));
        sessionFactoryBean.setConfigLocation(resolver.getResource("classpath:mybatisConfig.xml"));
        return sessionFactoryBean;
    }

    /**
     * 配置mybatis 会话工厂
     */
    @Bean
    public MapperScannerConfigurer MapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setSqlSessionFactoryBeanName("sessionFactoryBean");
        configurer.setBasePackage("com.heibaiying.dao");
        return configurer;
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
@ContextConfiguration(classes = {DatabaseConfig.class})
public class MysqlDaoTest {

    @Autowired
    private MysqlDao mysqlDao;

    @Test
    public void get() {
        List<Relation> relations = mysqlDao.queryById("691");
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
@ContextConfiguration(classes = {DatabaseConfig.class})
public class OracleDaoTest {

    @Autowired
    private OracleDao oracleDao;

    @Test
    public void get() {
        List<Flow> flows = oracleDao.queryById(217584603977429772L);
        if (flows != null) {
            for (Flow flow : flows) {
                System.out.println(flow.getId() + " " + flow.getPlugId());
            }
        }
    }
}

```


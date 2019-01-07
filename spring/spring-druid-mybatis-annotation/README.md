# spring +druid+ mybatis（注解方式）

### 项目目录结构

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-druid-mybatis-annotation.png"/> </div>

#### 1、创建maven工程，除了Spring基本依赖外，还需要导入mybatis和druid的相关依赖

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
<!--druid 依赖-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.12</version>
</dependency>
```

#### 2、新建 DispatcherServletInitializer.java继承自AbstractAnnotationConfigDispatcherServletInitializer,等价于我们在web.xml中配置的前端控制器

```java
public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{ServletConfig.class};
    }

    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
```

#### 3、基础servlet 3.0的支持，可以采用注解的方式注册druid的servlet和filter 

​     注：关于servlet 更多注解支持可以查看[Servlet 规范文档](https://github.com/heibaiying/spring-samples-for-all/blob/master/referenced%20documents/Servlet3.1%E8%A7%84%E8%8C%83%EF%BC%88%E6%9C%80%E7%BB%88%E7%89%88%EF%BC%89.pdf)中**8.1小节 注解和可插拔性** 

```java
/**
 * @author : heibaiying
 * @description : 配置监控页面用户名密码
 */
@WebServlet(urlPatterns = "/druid/*",
        initParams={
                @WebInitParam(name="resetEnable",value="true"),
                @WebInitParam(name="loginUsername",value="druid"),
                @WebInitParam(name="loginPassword",value="druid")
        })
public class DruidStatViewServlet extends StatViewServlet {
}

```

```java

/**
 * @author : heibaiying
 * @description : WebStatFilter用于采集web-jdbc关联监控的数据
 */
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",
        initParams={
                @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
        }
)
public class DruidStatFilter extends WebStatFilter {

}

```

#### 4、在resources文件夹下新建数据库配置文件mysql.properties、oracle.properties

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
oracle.url=jdbc:oracle:thin:@//IP地址:端口号/数据库实例名
oracle.username=用户名
oracle.password=密码
```

#### 5、在新建数据库配置映射类DataSourceConfig.java

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

#### 6、新建ServletConfig.java，进行数据库相关配置

```java
/**
 * @author : heibaiying
 */
@Configuration
@EnableTransactionManagement // 开启声明式事务处理 等价于xml中<tx:annotation-driven/>
@EnableWebMvc
@ComponentScan(basePackages = {"com.heibaiying.*"})
public class ServletConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源处理器
     */
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * 配置druid 数据源
     */
    @Bean
    public DruidDataSource dataSource(DataSourceConfig sourceConfig) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(sourceConfig.getUrl());
        dataSource.setUsername(sourceConfig.getUsername());
        dataSource.setPassword(sourceConfig.getPassword());

        // 配置获取连接等待超时的时间
        dataSource.setMaxWait(60000);

        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(2000);

        // 配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(600000);
        dataSource.setMaxEvictableIdleTimeMillis(900000);

        /* validationQuery 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。
         * 但是在 oracle 数据库下需要写成 select 'x' from dual 不然实例化数据源的时候就会失败,
         * 这是由于oracle 和 mysql 语法间的差异造成的
         */
        dataSource.setValidationQuery("select 'x'");

        // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        dataSource.setTestWhileIdle(true);
        // 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        dataSource.setTestOnBorrow(false);
        // 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestOnReturn(false);

        // 连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作。
        dataSource.setPhyMaxUseCount(100000);

        /*配置监控统计拦截的filters Druid连接池的监控信息主要是通过StatFilter 采集的，
         采集的信息非常全面，包括SQL执行、并发、慢查、执行时间区间分布等*/
        dataSource.setFilters("stat");

        return dataSource;
    }


    /**
     * 配置mybatis 会话工厂
     *
     * @param dataSource 这个参数的名称需要保持和上面方法名一致 才能自动注入,因为
     *                   采用@Bean注解生成的bean 默认采用方法名为名称，当然也可以在使用@Bean时指定name属性
     */
    @Bean
    public SqlSessionFactoryBean sessionFactoryBean(DruidDataSource dataSource) throws IOException {
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
    public DataSourceTransactionManager transactionManager(DruidDataSource dataSource) {
        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(dataSource);
        return manager;
    }

}

```

#### 7、新建mybtais 配置文件，按需要进行额外参数配置， 更多settings配置项可以参考[官方文档](http://www.mybatis.org/mybatis-3/zh/configuration.html)

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

#### 8、新建查询接口及其对应的mapper文件

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

#### 9.新建测试controller进行测试

```java
@RestController
public class MysqlController {

    @Autowired
    private MysqlDao mysqlDao;

    @GetMapping("relation/{id}")
    public String get(@PathVariable(name = "id") String id) {
        return mysqlDao.queryById(id).get(0).toString();
    }
}

```

```java
@RestController
public class OracleController {

    @Autowired
    private OracleDao oracleDao;

    @GetMapping("flow/{id}")
    public String get(@PathVariable(name = "id") Long id) {
        return oracleDao.queryById(id).get(0).toString();
    }
}

```

#### 10、druid 监控页面访问地址http://localhost:8080/druid/index.html

![druid控制台](https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/druid%E6%8E%A7%E5%88%B6%E5%8F%B0.png)



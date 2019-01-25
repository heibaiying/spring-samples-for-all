package com.heibaiying.springboot.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.heibaiying.springboot.constant.Data;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : heibaiying
 * @description : 多数据源配置
 * 需要特别强调的是以下任意的DataSource的bean都不要用@Primary去修饰，包括动态数据源。原因是在事务下,spring在DataSourceUtils类中doGetConnection方法中判断获取当前数据源是否持有连接的时候，
 * 总是会传入用@Primary修饰的数据源，如果一个事务下有多个数据源，这种情况下就会出现connection复用，无法切换数据源, 详见README.md
 */
@Configuration
@MapperScan(basePackages = DataSourceFactory.BASE_PACKAGES, sqlSessionTemplateRef = "sqlSessionTemplate")
public class DataSourceFactory {

    static final String BASE_PACKAGES = "com.heibaiying.springboot.dao";

    private static final String MAPPER_LOCATION = "classpath:mappers/*.xml";


    /***
     * 创建 DruidXADataSource 1
     */
    @Bean
    @ConfigurationProperties("spring.datasource.druid.db1")
    public DataSource DruidDataSourceOne() {
        return new DruidXADataSource();
    }

    /***
     * 创建 DruidXADataSource 2
     */
    @Bean
    @ConfigurationProperties("spring.datasource.druid.db2")
    public DataSource DruidDataSourceTwo() {
        return new DruidXADataSource();
    }

    /**
     * 创建支持XA事务的数据源1，不要用@Primary去修饰
     */
    @Bean
    public DataSource dataSourceOne(DataSource DruidDataSourceOne) {
        AtomikosDataSourceBean sourceBean = new AtomikosDataSourceBean();
        sourceBean.setXaDataSource((DruidXADataSource) DruidDataSourceOne);
        sourceBean.setUniqueResourceName("db1");
        return sourceBean;
    }

    /**
     * 创建支持XA事务的数据源2，不要用@Primary去修饰
     */
    @Bean
    public DataSource dataSourceTwo(DataSource DruidDataSourceTwo) {
        AtomikosDataSourceBean sourceBean = new AtomikosDataSourceBean();
        sourceBean.setXaDataSource((DruidXADataSource) DruidDataSourceTwo);
        sourceBean.setUniqueResourceName("db2");
        return sourceBean;
    }


    /**
     * 设置多数据源，不要用@Primary去修饰
     */
    @Bean
    public DataSource dynamicDataSource(DataSource dataSourceOne, DataSource dataSourceTwo) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        // 设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSourceOne);
        // 设置多数据源
        Map<Object, Object> dsMap = new HashMap<>();
        dsMap.put(Data.DATASOURCE1, dataSourceOne);
        dsMap.put(Data.DATASOURCE2, dataSourceTwo);

        dynamicDataSource.setTargetDataSources(dsMap);
        return dynamicDataSource;
    }


    /**
     * @param dataSourceOne 数据源1
     * @return 数据源1的会话工厂
     */
    @Bean
    public SqlSessionFactory sqlSessionFactoryOne(DataSource dataSourceOne)
            throws Exception {
        return createSqlSessionFactory(dataSourceOne);
    }


    /**
     * @param dataSourceTwo 数据源2
     * @return 数据源2的会话工厂
     */
    @Bean
    public SqlSessionFactory sqlSessionFactoryTwo(DataSource dataSourceTwo)
            throws Exception {
        return createSqlSessionFactory(dataSourceTwo);
    }


    /***
     * sqlSessionTemplate与Spring事务管理一起使用，以确保使用的实际SqlSession是与当前Spring事务关联的,
     * 此外它还管理会话生命周期，包括根据Spring事务配置根据需要关闭，提交或回滚会话
     * @param sqlSessionFactoryOne 数据源1
     * @param sqlSessionFactoryTwo 数据源2
     */
    @Bean
    public CustomSqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactoryOne,
                                                       SqlSessionFactory sqlSessionFactoryTwo) {
        Map<Object, SqlSessionFactory> sqlSessionFactoryMap = new HashMap<>();
        sqlSessionFactoryMap.put(Data.DATASOURCE1, sqlSessionFactoryOne);
        sqlSessionFactoryMap.put(Data.DATASOURCE2, sqlSessionFactoryTwo);

        CustomSqlSessionTemplate customSqlSessionTemplate = new CustomSqlSessionTemplate(sqlSessionFactoryOne);
        customSqlSessionTemplate.setTargetSqlSessionFactories(sqlSessionFactoryMap);
        return customSqlSessionTemplate;
    }

    /***
     * 自定义会话工厂
     * @param dataSource 数据源
     * @return :自定义的会话工厂
     */
    private SqlSessionFactory createSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        // 其他可配置项(包括是否打印sql,是否开启驼峰命名等)
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(StdOutImpl.class);
        factoryBean.setConfiguration(configuration);
        /* *
         * 采用个如下方式配置属性的时候一定要保证已经进行数据源的配置(setDataSource)和数据源和MapperLocation配置(setMapperLocations)
         * factoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
         * factoryBean.getObject().getConfiguration().setLogImpl(StdOutImpl.class);
         **/
        return factoryBean.getObject();
    }
}

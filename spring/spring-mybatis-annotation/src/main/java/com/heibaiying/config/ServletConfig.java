package com.heibaiying.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;


/**
 * @author : heibaiying
 */
@Configuration
@EnableTransactionManagement // 开启声明式事务处理 等价于xml中<tx:annotation-driven/>
@ComponentScan(basePackages = {"com.heibaiying.*"})
public class ServletConfig implements WebMvcConfigurer {

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

package com.heibaiying.springboot.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author : heibaiying
 * @description :
 */
public class AbstractDataSourceConfig {

    public DataSource getDataSource(Environment env, String prefix, String dataSourceName){
        Properties prop = build(env,prefix);
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        ds.setUniqueResourceName(dataSourceName);
        ds.setXaProperties(prop);
        return ds;
    }

    public Properties build(Environment env, String prefix) {
        Properties prop = new Properties();
        prop.put("url", env.getProperty(prefix + "url"));
        prop.put("username", env.getProperty(prefix + "username"));
        prop.put("password", env.getProperty(prefix + "password"));
        prop.put("driverClassName", env.getProperty(prefix + "driver-class-name", ""));
        prop.put("initialSize", env.getProperty(prefix + "initialSize", Integer.class));
        prop.put("maxActive", env.getProperty(prefix + "maxActive", Integer.class));
        prop.put("minIdle", env.getProperty(prefix + "minIdle", Integer.class));
        prop.put("maxWait", env.getProperty(prefix + "maxWait", Integer.class));
        //prop.put("poolPreparedStatements", env.getProperty(prefix + "poolPreparedStatements", Boolean.class));
        //prop.put("maxPoolPreparedStatementPerConnectionSize",env.getProperty(prefix + "maxPoolPreparedStatementPerConnectionSize", Integer.class));
        prop.put("validationQuery", env.getProperty(prefix + "validationQuery"));
        //prop.put("validationQueryTimeout", env.getProperty(prefix + "validationQueryTimeout", Integer.class));
        prop.put("testOnBorrow", env.getProperty(prefix + "testOnBorrow", Boolean.class));
        prop.put("testOnReturn", env.getProperty(prefix + "testOnReturn", Boolean.class));
        prop.put("testWhileIdle", env.getProperty(prefix + "testWhileIdle", Boolean.class));
        prop.put("timeBetweenEvictionRunsMillis", env.getProperty(prefix + "timeBetweenEvictionRunsMillis", Integer.class));
        prop.put("minEvictableIdleTimeMillis", env.getProperty(prefix + "minEvictableIdleTimeMillis", Integer.class));
        //prop.put("useGlobalDataSourceStat",env.getProperty(prefix + "useGlobalDataSourceStat", Boolean.class));
        //prop.put("filters", env.getProperty(prefix + "filters"));
        return prop;
    }
}

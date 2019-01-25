package com.heibaiying.springboot.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author : heibaiying
 * @description : 动态数据源配置
 */

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        System.out.println("当前数据库:"+DataSourceContextHolder.getDataKey());
        return DataSourceContextHolder.getDataKey();
    }
}

package com.heibaiying.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author : heibaiying
 * @description : 动态数据源配置
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        log.info("当前数据库:{}",DataSourceContextHolder.getDataKey());
        return DataSourceContextHolder.getDataKey();
    }
}

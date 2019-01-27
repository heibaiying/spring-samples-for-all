package com.heibaiying.springboot.config;

/**
 * @author : heibaiying
 * @description : 保证数据源切换的线程隔离
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    // 设置数据源名
    public static void setDataSourceKey(String dbName) {
        contextHolder.set(dbName);
    }

    // 获取数据源名
    public static String getDataSourceKey() {
        return (contextHolder.get());
    }

    // 清除数据源名
    public static void clearDataSourceKey() {
        contextHolder.remove();
    }
}

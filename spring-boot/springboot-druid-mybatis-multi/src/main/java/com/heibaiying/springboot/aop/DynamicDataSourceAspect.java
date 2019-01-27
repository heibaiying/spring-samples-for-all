package com.heibaiying.springboot.aop;

import com.heibaiying.springboot.config.DataSourceContextHolder;
import com.heibaiying.springboot.constant.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author : heibaiying
 * @description : 动态方式去切换数据源
 */
@Aspect
@Component
public class DynamicDataSourceAspect {

    @Pointcut(value = "execution(* com.heibaiying.springboot.dao.*.*(..))")
    public void dataSourcePointCut() {
    }

    @Before(value = "dataSourcePointCut()")
    public void beforeSwitchDS(JoinPoint point) {

        Object[] args = point.getArgs();

        if (args == null || args.length < 1 || !Data.DATASOURCE2.equals(args[0])) {
            DataSourceContextHolder.setDataSourceKey(Data.DATASOURCE1);
        } else {
            DataSourceContextHolder.setDataSourceKey(Data.DATASOURCE2);
        }
    }

    @After(value = "dataSourcePointCut()")
    public void afterSwitchDS(JoinPoint point) {
        DataSourceContextHolder.clearDataSourceKey();
    }
}

package com.heibaiying.dao;

import com.heibaiying.bean.Flow;
import com.heibaiying.config.DispatcherServletInitializer;
import com.heibaiying.config.ServletConfig;
import com.heibaiying.dao.impl.OracleDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author : heibaiying
 * @description :
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DispatcherServletInitializer.class, ServletConfig.class})
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

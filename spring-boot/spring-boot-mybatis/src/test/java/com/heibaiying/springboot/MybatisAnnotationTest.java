package com.heibaiying.springboot;

import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.dao.ProgrammerDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/***
 * @description: 注解Sql测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisAnnotationTest {

    @Autowired
    private ProgrammerDao programmerDao;

    @Test
    public void save() {
        programmerDao.save(new Programmer("xiaominng", 12, 3467.34f, new Date()));
        programmerDao.save(new Programmer("xiaominng", 12, 3467.34f, new Date()));
    }

    @Test
    public void modify() {
        programmerDao.modify(new Programmer(1, "xiaolan", 21, 347.34f, new Date()));
    }

    @Test
    public void selectByCondition() {
        Programmer programmers = programmerDao.selectById(1);
        System.out.println(programmers);
    }

    @Test
    public void delete() {
        programmerDao.delete(3);
        Programmer programmers = programmerDao.selectById(3);
        Assert.assertNull(programmers);
    }

}


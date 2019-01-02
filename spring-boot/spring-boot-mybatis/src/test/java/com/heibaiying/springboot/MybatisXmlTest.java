package com.heibaiying.springboot;

import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.dao.ProgrammerDao;
import com.heibaiying.springboot.dao.ProgrammerMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/***
 * @description: xml Sql测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisXmlTest {

    @Autowired
    private ProgrammerMapper mapper;

    @Test
    public void save() {
        mapper.save(new Programmer("xiaominng", 12, 3467.34f, new Date()));
        mapper.save(new Programmer("xiaominng", 12, 3467.34f, new Date()));
    }

    @Test
    public void modify() {
        mapper.modify(new Programmer(1, "xiaohong", 112, 347.34f, new Date()));
    }

    @Test
    public void selectByCondition() {
        Programmer programmers = mapper.selectById(1);
        System.out.println(programmers);
    }

    @Test
    public void delete() {
        mapper.delete(2);
        Programmer programmers = mapper.selectById(2);
        Assert.assertNull(programmers);
    }
}


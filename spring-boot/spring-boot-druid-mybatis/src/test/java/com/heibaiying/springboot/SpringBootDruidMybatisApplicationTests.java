package com.heibaiying.springboot;

import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.dao.ProgrammerDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDruidMybatisApplicationTests {

    @Autowired
    private ProgrammerDao  programmerDao;

    @Test
    public void contextLoads() {
        List<Programmer> programmers = programmerDao.selectAll();
        programmers.forEach(System.out::println);
    }
}


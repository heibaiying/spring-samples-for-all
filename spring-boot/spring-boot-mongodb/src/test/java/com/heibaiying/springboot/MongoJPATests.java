package com.heibaiying.springboot;

import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.repository.ProgrammerRepository;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoJPATests {

    @Autowired
    private ProgrammerRepository repository;

    @Test
    public void insert() {
        // 单条插入
        repository.save(new Programmer("python", 23, 21832.34f, new Date()));
        // 批量插入
        List<Programmer> programmers = new ArrayList<Programmer>();
        programmers.add(new Programmer("java", 21, 52200.21f, new Date()));
        programmers.add(new Programmer("Go", 34, 500.21f, new Date()));
        repository.saveAll(programmers);
    }

    // 条件查询
    @Test
    public void select() {
        Programmer java = repository.findByNameAndAge("java", 21);
        Assert.assertEquals(java.getSalary(), 52200.21f, 0.01);
    }


    // 更新数据
    @Test
    public void MUpdate() {
        repository.save(new Programmer("Go", 8, 500.21f, new Date()));
        Programmer go = repository.findAllByName("Go");
        Assert.assertEquals(go.getAge(), 8);
    }

    // 删除指定数据
    @Test
    public void delete() {
        repository.deleteAllByName("python");
        Optional<Programmer> python = repository.findById("python");
        Assert.assertFalse(python.isPresent());
    }

}


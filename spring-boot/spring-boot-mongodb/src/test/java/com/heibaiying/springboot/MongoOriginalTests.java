package com.heibaiying.springboot;

import com.heibaiying.springboot.bean.Programmer;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoOriginalTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void insert() {
        // 单条插入
        mongoTemplate.insert(new Programmer("xiaoming", 12, 5000.21f, new Date()));
        List<Programmer> programmers = new ArrayList<Programmer>();
        // 批量插入
        programmers.add(new Programmer("xiaohong", 21, 52200.21f, new Date()));
        programmers.add(new Programmer("xiaolan", 34, 500.21f, new Date()));
        mongoTemplate.insert(programmers, Programmer.class);
    }

    // 条件查询
    @Test
    public void select() {
        Criteria criteria = new Criteria();
        criteria.andOperator(where("name").is("xiaohong"), where("age").is(21));
        Query query = new Query(criteria);
        Programmer one = mongoTemplate.findOne(query, Programmer.class);
        System.out.println(one);
    }


    // 更新数据
    @Test
    public void MUpdate() {
        UpdateResult updateResult = mongoTemplate.updateMulti(query(where("name").is("xiaoming")), update("age", 35), Programmer.class);
        System.out.println("更新记录数：" + updateResult.getModifiedCount());
    }

    // 删除指定数据
    @Test
    public void delete() {
        DeleteResult result = mongoTemplate.remove(query(where("name").is("xiaolan")), Programmer.class);
        System.out.println("影响记录数：" + result.getDeletedCount());
        System.out.println("是否成功：" + result.wasAcknowledged());
    }

}


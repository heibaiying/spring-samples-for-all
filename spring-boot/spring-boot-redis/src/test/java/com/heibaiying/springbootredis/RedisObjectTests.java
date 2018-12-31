package com.heibaiying.springbootredis;

import com.heibaiying.springbootredis.bean.Programmer;
import com.heibaiying.springbootredis.redis.RedisObjectOperation;
import com.heibaiying.springbootredis.redis.RedisOperation;
import org.assertj.core.util.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisObjectTests {

    @Autowired
    private RedisObjectOperation redisOperation;

    @Test
    public void StringOperation() {
        Programmer programmer = new Programmer("小明", 12, 3334.3f, new Date());
        redisOperation.ObjectSet("programmer", programmer);
        Programmer objectGet = (Programmer) redisOperation.ObjectGet("programmer");
        Assert.assertEquals(objectGet, programmer);
    }

    @Test
    public void ListOperation() {
        Programmer programmer01 = new Programmer("小明", 12, 3334.3f, new Date());
        Programmer programmer02 = new Programmer("小红", 12, 3334.3f, new Date());
        redisOperation.ListSet("programmerList", Arrays.asList(programmer01, programmer02));
        Programmer programmer = (Programmer) redisOperation.ListLeftPop("programmerList");
        Assert.assertEquals(programmer.getName(), "小红");
    }

    /*
     * 需要注意的是Redis的集合（set）不仅不允许有重复元素，并且集合中的元素是无序的，
     * 不能通过索引下标获取元素。哪怕你在java中传入的集合是有序的newLinkedHashSet，但是实际在Redis存储的还是无序的集合
     */
    @Test
    public void SetOperation() {
        Programmer programmer01 = new Programmer("小明", 12, 3334.3f, new Date());
        Programmer programmer02 = new Programmer("小爱", 12, 3334.3f, new Date());
        Programmer programmer03 = new Programmer("小红", 12, 3334.3f, new Date());
        redisOperation.SetSet("programmerSet", Sets.newLinkedHashSet(programmer01, programmer02, programmer03));
        Programmer programmer = (Programmer) redisOperation.SetPop("programmerSet");
        Assert.assertNotNull(programmer);
    }

}


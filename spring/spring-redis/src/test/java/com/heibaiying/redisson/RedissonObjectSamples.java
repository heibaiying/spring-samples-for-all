package com.heibaiying.redisson;

import com.heibaiying.bean.Programmer;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author : heibaiying
 * @description :redisson 对象序列化与反序列化
 */


@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
public class RedissonObjectSamples {

    @Autowired
    private RedissonClient redissonClient;

    // Redisson的对象编码类是用于将对象进行序列化和反序列化 默认采用Jackson

    @Test
    public void Set() {
        RBucket<Programmer> rBucket = redissonClient.getBucket("programmer");
        rBucket.set(new Programmer("xiaoming", 12, 5000.21f, new Date()));
        redissonClient.shutdown();
        //存储结果: {"@class":"com.heibaiying.bean.Programmer","age":12,"birthday":["java.util.Date",1545714986590],"name":"xiaoming","salary":5000.21}
    }

    @Test
    public void Get() {
        RBucket<Programmer> rBucket = redissonClient.getBucket("programmer");
        System.out.println(rBucket.get());
    }

    @After
    public void close() {
        redissonClient.shutdown();
    }
}

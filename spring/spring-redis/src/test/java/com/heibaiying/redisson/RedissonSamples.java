package com.heibaiying.redisson;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author : heibaiying
 * @description :redisson 操作普通对象
 */


@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
public class RedissonSamples {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void Set() {
        // key 存在则更新 不存在则删除
        RBucket<String> rBucket = redissonClient.getBucket("redisson");
        rBucket.set("firstValue");
        redissonClient.shutdown();
    }

    @Test
    public void Get() {
        // key 存在则更新 不存在则删除
        RBucket<String> rBucket = redissonClient.getBucket("redisson");
        System.out.println(rBucket.get());
    }

    @Test
    public void SetEx() {
        // key 存在则更新 不存在则删除
        RBucket<String> rBucket = redissonClient.getBucket("redissonEx");
        rBucket.set("我在十秒后会消失", 10, TimeUnit.SECONDS);
    }


    @After
    public void close() {
        redissonClient.shutdown();
    }
}

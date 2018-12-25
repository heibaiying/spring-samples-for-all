package com.heibaiying.jedis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author : heibaiying
 * @description :redis 单机版测试
 */


@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:springApplication.xml"})
public class JedisSamples {

    @Autowired
    private Jedis jedis;

    @Test
    public void Set() {
        jedis.set("hello", "spring");
    }

    @Test
    public void Get() {
        String s = jedis.get("hello");
        System.out.println(s);
    }

    @Test
    public void setEx() {
        String s = jedis.setex("spring", 10, "我会在10秒后过期");
        System.out.println(s);
    }

}

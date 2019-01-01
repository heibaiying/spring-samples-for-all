package com.heibaiying.springboot;

import com.heibaiying.springboot.bean.Programmer;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * @author : heibaiying
 * @description :Memcached 序列化与反序列化
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemcacheObjectTest {

    @Autowired
    private MemcachedClient memcachedClient;

    @Test
    public void operate() throws InterruptedException, MemcachedException, TimeoutException {
        memcachedClient.set("programmer", 0, new Programmer("xiaoming", 12, 5000.21f, new Date()));
        Programmer programmer = memcachedClient.get("programmer");
        System.out.println("hello ," + programmer.getName());
        memcachedClient.delete("programmer");
        programmer = memcachedClient.get("programmer");
        Assert.assertNull(programmer);
    }
}

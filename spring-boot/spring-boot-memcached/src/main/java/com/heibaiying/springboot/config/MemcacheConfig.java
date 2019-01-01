package com.heibaiying.springboot.config;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.TextCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : heibaiying
 */
@Configuration
public class MemcacheConfig {

    /*
     * Memcached 单机版本简单配置
     */
    @Bean
    public MemcachedClient memcachedClient() {
        XMemcachedClientBuilder builder = new XMemcachedClientBuilder("192.168.0.108:11211");
        MemcachedClient memcachedClient = null;
        try {
            memcachedClient = builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return memcachedClient;
    }

    /*
     * Memcached 集群版本配置
     */

    /*@Bean*/
    public MemcachedClient memcachedClientForCluster() {

        List<InetSocketAddress> addressList = new ArrayList<>();
        addressList.add(new InetSocketAddress("192.168.0.108", 11211));
        addressList.add(new InetSocketAddress("192.168.0.108", 11212));
        // 赋予权重
        int[] weights = {1, 2};
        XMemcachedClientBuilder builder = new XMemcachedClientBuilder(addressList, weights);
        // 设置连接池大小
        builder.setConnectionPoolSize(10);
        // 协议工厂
        builder.setCommandFactory(new TextCommandFactory());
        // 分布策略，一致性哈希KetamaMemcachedSessionLocator或者ArraySessionLocator(默认)
        builder.setSessionLocator(new KetamaMemcachedSessionLocator());
        // 设置序列化器
        builder.setTranscoder(new SerializingTranscoder());
        MemcachedClient memcachedClient = null;
        try {
            memcachedClient = builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return memcachedClient;
    }


}
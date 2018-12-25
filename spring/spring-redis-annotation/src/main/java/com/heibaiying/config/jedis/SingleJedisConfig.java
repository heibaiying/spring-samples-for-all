package com.heibaiying.config.jedis;

import com.heibaiying.config.RedisProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author : heibaiying
 * @description : Jedis 单机配置
 */
@Configuration
@ComponentScan(value = "com.heibaiying.*")
public class SingleJedisConfig {

    @Bean
    public JedisPool jedisPool(RedisProperty property) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(property.getMaxIdle());
        poolConfig.setMaxTotal(property.getMaxTotal());
        return new JedisPool(poolConfig, property.getHost(), property.getPort(), property.getTimeout());
    }

    @Bean(destroyMethod = "close")
    @Scope(value = SCOPE_PROTOTYPE)
    public Jedis jedis(JedisPool jedisPool) {
        return jedisPool.getResource();
    }
}

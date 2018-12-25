package com.heibaiying.config.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Configuration;

/**
 * @author : heibaiying
 * @description : redisson 集群配置
 */
@Configuration
public class ClusterRedissonConfig {

    //@Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:6380")
                .addNodeAddress("redis://127.0.0.1:6381");
        return Redisson.create(config);
    }

}

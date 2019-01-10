package com.heibaiying.actuator.aggregator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : heibaiying
 * @description : 对所有的自定义健康指标进行聚合，按照自定义规则返回总和健康状态
 */
@Component
public class CustomHealthAggregator implements HealthAggregator {

    @Override
    public Health aggregate(Map<String, Health> healths) {
        for (Health health : healths.values()) {
            // 聚合规则可以自定义,这里假设我们自定义的监控状态中有一项FATAL,就认为整个服务都是不可用的,否则认为整个服务是可用的
            if (health.getStatus().getCode().equals("FATAL")) {
                return Health.status("FATAL").withDetail("error code", "综合判断后服务宕机").build();
            }
        }
        return Health.up().build();
    }
}

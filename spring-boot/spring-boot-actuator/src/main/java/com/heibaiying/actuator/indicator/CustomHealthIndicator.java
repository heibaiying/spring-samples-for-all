package com.heibaiying.actuator.indicator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : heibaiying
 * @description : 自定义健康检查指标
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        double random = Math.random();
        // 这里用随机数模拟健康检查的结果
        if (random > 0.5) {
            return Health.status("FATAL").withDetail("error code", "某健康专项检查失败").build();
        } else {
            return Health.up().withDetail("success code", "自定义检查一切正常").build();
        }

    }
}

package com.heibaiying.consumer.feign;

import com.heibaiying.common.feign.ProductFeign;
import com.heibaiying.consumer.config.FeignConfig;
import com.heibaiying.consumer.feign.impl.CProductFeignImpl;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author : heibaiying
 * @description : 声明式接口调用
 */
@FeignClient(value = "producer",configuration = FeignConfig.class,fallback = CProductFeignImpl.class)
public interface CProductFeign extends ProductFeign {

}

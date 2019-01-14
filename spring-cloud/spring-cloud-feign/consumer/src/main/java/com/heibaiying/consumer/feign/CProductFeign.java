package com.heibaiying.consumer.feign;

import com.heibaiying.common.feign.ProductFeign;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author : heibaiying
 * @description : 声明式接口调用
 */
@FeignClient("producer")
public interface CProductFeign extends ProductFeign {

}

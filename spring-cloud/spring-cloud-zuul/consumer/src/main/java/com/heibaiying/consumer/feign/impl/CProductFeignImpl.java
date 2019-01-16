package com.heibaiying.consumer.feign.impl;

import com.heibaiying.common.bean.Product;
import com.heibaiying.consumer.feign.CProductFeign;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : heibaiying
 * @description : 定义发生错误时候的熔断处理。除了继承自CProductFeign,还需要用@Component声明为spring的组件
 */
@Component
public class CProductFeignImpl implements CProductFeign {

    @Override
    public List<Product> productList() {
        return new ArrayList<>();
    }

    @Override
    public Product productDetail(int id) {
        return null;
    }

    @Override
    public void save(Product product) {

    }
}

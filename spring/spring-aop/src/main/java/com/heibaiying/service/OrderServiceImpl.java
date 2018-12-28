package com.heibaiying.service;

import com.heibaiying.bean.Order;

import java.util.Date;

/**
 * @author : heibaiying
 * @description :
 */
public class OrderServiceImpl implements OrderService {

    public Order queryOrder(Long id) {
        return new Order(id, "product", new Date());
    }

    public Order createOrder(Long id, String productName) {
        // 模拟抛出异常
        // int j = 1 / 0;
        return new Order(id, "new Product", new Date());
    }
}

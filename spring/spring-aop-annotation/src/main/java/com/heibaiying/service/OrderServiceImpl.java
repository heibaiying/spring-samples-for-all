package com.heibaiying.service;

import com.heibaiying.bean.Order;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author : heibaiying
 * @description : 订单查询接口实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    public Order queryOrder(Long id) {
        return new Order(id, "product", new Date());
    }

    public Order createOrder(Long id, String productName) {
        // 模拟抛出异常
        // int j = 1 / 0;
        return new Order(id, "new Product", new Date());
    }

    public boolean deleteOrder(Long id) {
        return true;
    }
}

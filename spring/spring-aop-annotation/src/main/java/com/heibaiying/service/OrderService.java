package com.heibaiying.service;

import com.heibaiying.bean.Order;

/**
 * @author : heibaiying
 * @description : 订单查询接口
 */
public interface OrderService {

    Order queryOrder(Long id);

    Order createOrder(Long id, String productName);

    boolean deleteOrder(Long id);
}

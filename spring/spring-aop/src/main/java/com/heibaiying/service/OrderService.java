package com.heibaiying.service;

import com.heibaiying.bean.Order;

import java.util.Date;

/**
 * @author : heibaiying
 * @description :
 */
public interface OrderService {

    Order queryOrder(Long id);

    Order createOrder(Long id, String productName);
}

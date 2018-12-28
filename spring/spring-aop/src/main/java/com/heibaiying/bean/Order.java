package com.heibaiying.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : heibaiying
 * @description : 订单实体类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private long id;

    private String productName;

    private Date orderTime;
}

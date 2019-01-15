package com.heibaiying.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : heibaiying
 * @description : 产品实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {

    // 产品序列号
    private long id;

    // 产品名称
    private String name;

    // 是否贵重品
    private Boolean isPrecious;

    //生产日期
    private Date dateInProduced;

    //产品价格
    private float price;
}

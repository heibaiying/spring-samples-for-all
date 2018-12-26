package com.heibaiying.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : heibaiying
 * @description :
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductManager {

    private String name;

    private int age;

    private Date birthday;
}

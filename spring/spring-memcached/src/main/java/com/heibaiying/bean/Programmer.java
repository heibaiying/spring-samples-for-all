package com.heibaiying.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : heibaiying
 * @description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Programmer implements Serializable {

    private String name;

    private int age;

    private float salary;

    private Date birthday;
}

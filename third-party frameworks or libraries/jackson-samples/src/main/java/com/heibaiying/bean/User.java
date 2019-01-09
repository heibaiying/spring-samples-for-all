package com.heibaiying.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : heibaiying
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;

    private int age;

    private float salary;

    private Date birthday;
}

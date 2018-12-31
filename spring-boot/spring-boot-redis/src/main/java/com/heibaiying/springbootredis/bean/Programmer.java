package com.heibaiying.springbootredis.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author : heibaiying
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Programmer implements Serializable {

    private String name;

    private int age;

    private float salary;

    private Date birthday;
}
package com.heibaiying.bean;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


/**
 * @author : heibaiying
 * @description :
 */
@Data
public class Programmer {

    @NotNull
    private String name;

    @Min(value = 0,message = "年龄不能为负数！" )
    private int age;

    @Min(value = 0,message = "薪酬不能为负数！" )
    private float salary;

    private String birthday;
}

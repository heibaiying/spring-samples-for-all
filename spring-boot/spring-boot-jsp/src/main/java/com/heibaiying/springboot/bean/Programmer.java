package com.heibaiying.springboot.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author : heibaiying
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Programmer {

    private String name;

    private int age;

    private float salary;

    private LocalDate birthday;
}

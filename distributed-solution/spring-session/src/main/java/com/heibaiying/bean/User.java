package com.heibaiying.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : heibaiying
 */
@Data
public class User implements Serializable {

    private long userId;
    private String username;
    private String password;

}

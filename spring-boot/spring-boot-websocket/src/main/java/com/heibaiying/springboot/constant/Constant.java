package com.heibaiying.springboot.constant;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 罗祥
 * @description :
 * @date :create in 2018/12/27
 */
public interface Constant {

    String USER_NAME="username";

    Map<String, Session> nameAndSession = new ConcurrentHashMap<>();
}

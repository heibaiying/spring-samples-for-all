package com.heibaiying.springboot.constant;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public interface Constant {

    String USER_NAME="username";

    Map<String, Session> nameAndSession = new ConcurrentHashMap<>();
}

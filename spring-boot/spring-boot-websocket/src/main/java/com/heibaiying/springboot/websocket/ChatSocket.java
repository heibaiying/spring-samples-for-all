package com.heibaiying.springboot.websocket;

import com.heibaiying.springboot.constant.Constant;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author : heibaiying
 */

@ServerEndpoint(value = "/socket/{username}")
@Component
public class ChatSocket {

    /**
     * 建立连接时候触发
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        // 这个方法是线程不安全的
        Constant.nameAndSession.putIfAbsent(username, session);
    }


    /**
     * 关闭连接时候触发
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        Constant.nameAndSession.remove(username);
    }

    /**
     * 处理消息
     */
    @OnMessage
    public void onMessage(Session session, String message, @PathParam("username") String username) throws UnsupportedEncodingException {
        // 防止中文乱码
        String msg = URLDecoder.decode(message, "utf-8");
        // 简单模拟群发消息
        Constant.nameAndSession.forEach((s, webSocketSession)
                -> {
            try {
                webSocketSession.getBasicRemote().sendText(username + " : " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

package com.heibaiying.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;

/**
 * @author : heibaiying
 * @description : 自定义消息处理器
 */
public class CustomerHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String stringMessage = new String(message.asBytes());
        System.out.println("服务端收到消息：" + stringMessage);
        session.sendMessage(new TextMessage(stringMessage+LocalDateTime.now()));
    }
}

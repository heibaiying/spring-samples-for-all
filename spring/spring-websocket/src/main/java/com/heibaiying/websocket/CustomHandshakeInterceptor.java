package com.heibaiying.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author : heibaiying
 * @description : 握手拦截器
 */
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    /*@Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        System.out.println(remoteAddress);
        return true;
    }*/

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}


package com.heibaiying.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author : heibaiying
 * @description : 可以按照需求实现权限拦截等功能
 */
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        InetAddress address = remoteAddress.getAddress();
        System.out.println(address);
        /*
         * 最后需要要显示调用父类方法，父类的beforeHandshake方法
         * 把ServerHttpRequest 中session中对应的值拷贝到WebSocketSession中。
         * 如果我们没有实现这个方法，我们在最后的handler处理中 是拿不到 session中的值
         * 作为测试 可以注释掉下面这一行 可以发现自定义处理器中session的username总是为空
         */
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}


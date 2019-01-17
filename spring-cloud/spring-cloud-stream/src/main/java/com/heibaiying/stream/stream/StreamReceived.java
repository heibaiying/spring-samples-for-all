package com.heibaiying.stream.stream;

import com.heibaiying.stream.bean.Programmer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : heibaiying
 * @description :消息的监听
 */

@Component
@EnableBinding(Custom.class)
@Slf4j
public class StreamReceived {

    @StreamListener(value = Custom.INPUT)
    public void simple(Object payload) {
        log.info("收到简单消息: {}", payload);
    }

    @StreamListener(value = Custom.INPUT)
    public void object(Programmer programmer) {
        log.info("收到对象消息: {}", programmer);
    }

    /**
     * 用 @Header 监听时候需要注意，指定名称的属性必须在消息头中存在 不然就会抛出异常 MessageHandlingException: Missing header 'XXXX' for method parameter type [class java.lang.String]
     */
    @StreamListener(value = Custom.INPUT)
    public void heads(@Payload Programmer programmer, @Headers Map<String, Object> map, @Header(name = "code") String code) {
        log.info("收到对象消息: {}", programmer);
        map.forEach((key, value) -> {
            log.info("消息头{}的值为{}", key, value);
        });
        log.info("绑定指定消息头: code = {}", code);
    }


    /**
     * 监听消息头key = 01 的消息
     */
    @StreamListener(target = Custom.INPUT, condition = "headers['key']=='01'")
    public void key01(@Payload Programmer programmer) {
        log.info("key01 监听器接收到消息: {}", programmer.getName());
    }

    /**
     * 监听消息头key = 02 的消息
     */
    @StreamListener(target = Custom.INPUT, condition = "headers['key']=='01'")
    public void key02(@Payload Programmer programmer) {
        log.info("key02 监听器接收到消息: {}", programmer.getName());
    }
}

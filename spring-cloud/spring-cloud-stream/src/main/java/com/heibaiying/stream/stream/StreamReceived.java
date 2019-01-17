package com.heibaiying.stream.stream;

import com.heibaiying.stream.bean.Programmer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : heibaiying
 * @description :消息的监听
 * 注意:  测试这个类的时候需要注释掉不必要的监听,因为对同一个通道存在多个监听，任何一个通道都不能存在返回值(如果有返回值应该指定出站目标) 我们下面的forward消息转发的方法是有返回值的
 * 否则会抛出异常: IllegalArgumentException: If multiple @StreamListener methods are listening to the same binding target, none of them may return a value
 */

@Component
@EnableBinding(CustomStream.class)
@Slf4j
public class StreamReceived {

    @StreamListener(value = CustomStream.INPUT)
    public void simple(Object payload) {
        log.info("收到简单消息: {}", payload);
    }

    @StreamListener(value = CustomStream.INPUT)
    public void object(Programmer programmer) {
        log.info("收到对象消息: {}", programmer);
    }

    /*
     * 用 @Header 监听时候需要注意，指定名称的属性必须在消息头中存在 不然就会抛出异常 MessageHandlingException: Missing header 'XXXX' for method parameter type [class java.lang.String]
     */
    @StreamListener(value = CustomStream.INPUT)
    public void heads(@Payload Programmer programmer, @Headers Map<String, Object> map, @Header(name = "code") String code) {
        log.info("收到对象消息: {}", programmer);
        map.forEach((key, value) -> {
            log.info("消息头{}的值为{}", key, value);
        });
        log.info("绑定指定消息头: code = {}", code);
    }


    /*
     * 监听消息头key = 01 的消息
     */
    @StreamListener(target = CustomStream.INPUT, condition = "headers['key']=='01'")
    public void key01(@Payload Programmer programmer) {
        log.info("key01 监听器接收到消息: {}", programmer.getName());
    }

    /*
     * 监听消息头key = 02 的消息
     */
    @StreamListener(target = CustomStream.INPUT, condition = "headers['key']=='01'")
    public void key02(@Payload Programmer programmer) {
        log.info("key02 监听器接收到消息: {}", programmer.getName());
    }

    /**
     * 消息转发
     */
    @StreamListener(target = CustomStream.INPUT)
    @SendTo(CustomStream.OUTPUT)
    public String forward(String payload){
        log.info("input forward: {}",payload);
        return "forward "+payload;
    }

    @StreamListener(target = CustomStream.OUTPUT)
    public void outSimpleListen(String payload){
        log.info("output 收到简单消息: {}", payload);
    }

}

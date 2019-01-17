package com.heibaiying.stream.controller;

import com.heibaiying.stream.bean.Programmer;
import com.heibaiying.stream.stream.Custom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : heibaiying
 * @description : 发送测试消息
 */
@RestController
public class MessageController {

    @Autowired
    private Custom custom;

    /***
     * 发送简单消息
     */
   @RequestMapping("sendSimpleMessage")
   public void sendSimpleMessage() {
       custom.input().send(MessageBuilder.withPayload("hell spring cloud stream").build());
   }


    /***
     * 发送消息体为对象的消息
     *
     */
    @RequestMapping("sendObject")
    public void sendObject() {
        Programmer programmer=new Programmer("pro",12,212.2f,new Date());
        custom.input().send(MessageBuilder.withPayload(programmer).build());
    }

    /**
     * 发送带有消息头的消息
     */
    @RequestMapping("sendWithHeads")
    public void sendWithHeads() {
        Programmer programmer=new Programmer("pro",12,212.2f,new Date());
        Map<String,Object> map=new HashMap<>();
        map.put("code","868686");
        MessageHeaders messageHeaders=new MessageHeaders(map);
        Message<Programmer> message= MessageBuilder.createMessage(programmer,messageHeaders);
        custom.input().send(message);
    }

    /**
     * 条件消息 可以看做是消息路由键的一种实现
     */
    @RequestMapping("sendWithKey")
    public void sendWithKey() {
        // 创建消息头key 为 01 的消息
        Programmer programmer=new Programmer("key01",12,212.2f,new Date());
        Map<String,Object> map=new HashMap<>();
        map.put("key","01");
        MessageHeaders messageHeaders=new MessageHeaders(map);
        Message<Programmer> message= MessageBuilder.createMessage(programmer,messageHeaders);
        custom.input().send(message);

        // 创建消息头key 为 02 的消息
        programmer.setName("key02");
        map.put("key","02");
        MessageHeaders messageHeaders02=new MessageHeaders(map);
        Message<Programmer> message02= MessageBuilder.createMessage(programmer,messageHeaders02);
        custom.input().send(message02);
    }
}

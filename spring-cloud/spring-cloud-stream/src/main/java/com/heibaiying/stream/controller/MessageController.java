package com.heibaiying.stream.controller;

import com.heibaiying.stream.bean.Programmer;
import com.heibaiying.stream.stream.CustomStream;
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
    private CustomStream customStream;

    /***
     * 1、发送简单消息
     */
   @RequestMapping("sendSimpleMessage")
   public void sendSimpleMessage() {
       customStream.input().send(MessageBuilder.withPayload("hello spring cloud stream").build());
   }


    /***
     * 2、发送消息体为对象的消息
     */
    @RequestMapping("sendObject")
    public void sendObject() {
        Programmer programmer=new Programmer("pro",12,212.2f,new Date());
        customStream.input().send(MessageBuilder.withPayload(programmer).build());
    }

    /**
     * 3、发送带有消息头的消息
     */
    @RequestMapping("sendWithHeads")
    public void sendWithHeads() {
        Programmer programmer=new Programmer("pro",12,212.2f,new Date());
        Map<String,Object> map=new HashMap<>();
        map.put("code","868686");
        MessageHeaders messageHeaders=new MessageHeaders(map);
        Message<Programmer> message= MessageBuilder.createMessage(programmer,messageHeaders);
        customStream.input().send(message);
    }

    /**
     * 4、条件消息 可以看做是消息路由键的一种实现
     */
    @RequestMapping("sendWithKey")
    public void sendWithKey() {
        // 创建消息头key 为 01 的消息
        Programmer programmer=new Programmer("key01",12,212.2f,new Date());
        Map<String,Object> map=new HashMap<>();
        map.put("key","01");
        MessageHeaders messageHeaders=new MessageHeaders(map);
        Message<Programmer> message= MessageBuilder.createMessage(programmer,messageHeaders);
        customStream.input().send(message);

        // 创建消息头key 为 02 的消息
        programmer.setName("key02");
        map.put("key","02");
        MessageHeaders messageHeaders02=new MessageHeaders(map);
        Message<Programmer> message02= MessageBuilder.createMessage(programmer,messageHeaders02);
        customStream.input().send(message02);
    }

    /**
     * 5、消息转发
     */
    @RequestMapping("forward")
    public void forward(){
        customStream.input().send(MessageBuilder.withPayload("hello spring cloud stream").build());
    }

    /**
     * 5、直接往output发消息
     */
    @RequestMapping("toOutPut")
    public void toOutPut(){
        customStream.output().send(MessageBuilder.withPayload("direct to output channel").build());
    }
}

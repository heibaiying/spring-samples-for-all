package com.heibaiying.rabbitmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : heibaiying
 * @description : 消息生产者
 */
@Component
@Slf4j
public class RabbitmqProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSimpleMessage( Map<String, Object> headers,Object message,
                                  String messageId, String exchangeName, String key) {
        // 自定义消息头
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        // 创建消息
        Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);
        // 确认的回调 确认消息是否到达 Broker 服务器
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            //当ack为false时才表示消息不可达，才需要进行对应的消息补偿机制
            log.info("correlationData：{} , ack:{}", correlationData.getId(),ack);
        });
        // 消息失败的回调
        rabbitTemplate.setReturnCallback((message1, replyCode, replyText, exchange, routingKey) -> {
            log.info("message:{}; replyCode{}; replyText{} ;", message1.getBody(), replyCode, replyText);
        });
        // 在实际中ID 应该是全局唯一 能够唯一标识消息
        CorrelationData correlationData = new CorrelationData(messageId);
        rabbitTemplate.convertAndSend(exchangeName, key, msg, correlationData);
    }
}

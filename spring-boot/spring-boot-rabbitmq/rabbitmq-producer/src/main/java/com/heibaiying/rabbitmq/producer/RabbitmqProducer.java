package com.heibaiying.rabbitmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
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

    public void sendSimpleMessage(Map<String, Object> headers, Object message,
                                  String messageId, String exchangeName, String key) {
        // 自定义消息头
        MessageHeaders messageHeaders = new MessageHeaders(headers);
        // 创建消息
        Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);
        /* 确认的回调 确认消息是否到达 Broker 服务器 其实就是是否到达交换器
           如果发送时候指定的交换器不存在 ack就是false 代表消息不可达 */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("correlationData：{} , ack:{}", correlationData.getId(), ack);
            if (!ack) {
                System.out.println("进行对应的消息补偿机制");
            }
        });
        /* 消息失败的回调
         * 例如消息已经到达交换器上，但路由键匹配任何绑定到该交换器的队列，会触发这个回调，此时 replyText: NO_ROUTE
         */
        rabbitTemplate.setReturnCallback((message1, replyCode, replyText, exchange, routingKey) -> {
            log.info("message:{}; replyCode: {}; replyText: {} ; exchange:{} ; routingKey:{}",
                    message1, replyCode, replyText, exchange, routingKey);
        });
        // 在实际中ID 应该是全局唯一 能够唯一标识消息 消息不可达的时候触发ConfirmCallback回调方法时可以获取该值，进行对应的错误处理
        CorrelationData correlationData = new CorrelationData(messageId);
        rabbitTemplate.convertAndSend(exchangeName, key, msg, correlationData);
    }
}

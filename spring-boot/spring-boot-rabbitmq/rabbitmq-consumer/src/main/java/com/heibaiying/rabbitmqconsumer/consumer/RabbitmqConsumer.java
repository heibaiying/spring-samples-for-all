package com.heibaiying.rabbitmqconsumer.consumer;


import com.heibaiying.constant.RabbitInfo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * @author : heibaiying
 * @description : 消息消费者
 */

@Component
@Slf4j
public class RabbitmqConsumer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitInfo.QUEUE_NAME, durable = RabbitInfo.QUEUE_DURABLE),
            exchange = @Exchange(value = RabbitInfo.EXCHANGE_NAME, type = RabbitInfo.EXCHANGE_TYPE),
            key = RabbitInfo.ROUTING_KEY)
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageHeaders headers = message.getHeaders();
        // 获取消息头信息和消息体
        log.info("msgInfo:{} ; payload:{} ", headers.get("msgInfo"), message.getPayload());
        //  DELIVERY_TAG 代表 RabbitMQ 向该Channel投递的这条消息的唯一标识ID，是一个单调递增的正整数
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        // 第二个参数代表是否一次签收多条,当该参数为 true 时，则可以一次性确认 DELIVERY_TAG 小于等于传入值的所有消息
        channel.basicAck(deliveryTag, false);
    }

}

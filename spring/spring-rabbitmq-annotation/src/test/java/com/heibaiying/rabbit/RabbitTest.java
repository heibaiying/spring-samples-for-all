package com.heibaiying.rabbit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : heibaiying
 * @description : 传输简单字符串
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RabbitBaseConfig.class)
public class RabbitTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage() {
        MessageProperties properties = new MessageProperties();

        String allReceived = "我的路由键 quick.orange.rabbit 符合queue1 和 queue2 的要求，我应该被两个监听器接收到";
        Message message1 = new Message(allReceived.getBytes(), properties);
        rabbitTemplate.send("topic01", "quick.orange.rabbit", message1);

        String firstReceived = "我的路由键 quick.orange.fox 只符合queue1 的要求，只能被queue 1 接收到";
        Message message2 = new Message(firstReceived.getBytes(), properties);
        rabbitTemplate.send("topic01", "quick.orange.fox", message2);

        String secondReceived = "我的路由键 lazy.brown.fox 只符合queue2 的要求，只能被queue 2 接收到";
        Message message3 = new Message(secondReceived.getBytes(), properties);
        rabbitTemplate.send("topic01", "lazy.brown.fox", message3);

        String notReceived = "我的路由键 quick.brown.fox 不符合 topic1 任何绑定队列的要求，你将看不到我";
        Message message4 = new Message(notReceived.getBytes(), properties);
        rabbitTemplate.send("topic01", "quick.brown.fox", message4);

        /*
         * SecondQueue收到消息:我的路由键 quick.orange.rabbit 符合queue1 和 queue2 的要求，我应该被两个监听器接收到
         * FirstQueue收到消息:我的路由键 quick.orange.rabbit 符合queue1 和 queue2 的要求，我应该被两个监听器接收到
         * FirstQueue收到消息:我的路由键 quick.orange.fox 只符合queue1 的要求，只能被queue 1 接收到
         * SecondQueue收到消息:我的路由键 lazy.brown.fox 只符合queue2 的要求，只能被queue 2 接收到
         */
    }
}

package com.heibaiying.rabbitmq;

import com.heibaiying.bean.Programmer;
import com.heibaiying.constant.RabbitBeanInfo;
import com.heibaiying.constant.RabbitInfo;
import com.heibaiying.rabbitmq.producer.RabbitmqProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqProducerTests {

    @Autowired
    private RabbitmqProducer producer;

    @Test
    public void send() {
        Map<String, Object> heads = new HashMap<>();
        heads.put("msgInfo", "自定义消息头信息");
        // 模拟生成消息ID,在实际中应该是全局唯一的 消息不可达时候可以在setConfirmCallback回调中取得，可以进行对应的重发或错误处理
        String id = String.valueOf(Math.round(Math.random() * 10000));
        producer.sendSimpleMessage(heads, "hello Spring", id, RabbitInfo.EXCHANGE_NAME, "springboot.simple.abc");
    }


    @Test
    public void sendBean() {
        String id = String.valueOf(Math.round(Math.random() * 10000));
        Programmer programmer = new Programmer("xiaoMing", 12, 12123.45f, new Date());
        producer.sendSimpleMessage(null, programmer, id, RabbitBeanInfo.EXCHANGE_NAME, RabbitBeanInfo.ROUTING_KEY);
    }

}


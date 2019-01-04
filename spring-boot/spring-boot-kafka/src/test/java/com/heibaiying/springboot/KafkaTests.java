package com.heibaiying.springboot;

import com.alibaba.fastjson.JSON;
import com.heibaiying.springboot.Producer.KafKaCustomrProducer;
import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.constant.Topic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTests {

    @Autowired
    private KafKaCustomrProducer producer;

    /***
     * 发送消息体为基本类型的消息
     */
    @Test
    public void sendSimple() {
        producer.sendMessage(Topic.SIMPLE, "hello spring boot kafka");
    }

    /***
     * 发送消息体为bean的消息
     */
    @Test
    public void sendBean() {
        Programmer programmer = new Programmer("xiaoming", 12, 21212.33f, new Date());
        producer.sendMessage(Topic.BEAN, JSON.toJSON(programmer).toString());
    }


    /***
     * 多消费者组、组中多消费者对同一主题的消费情况
     */
    @Test
    public void sendGroup() {
        for (int i = 0; i < 5; i++) {
            producer.sendMessage(Topic.GROUP, "hello group " + i);
        }
    }
}


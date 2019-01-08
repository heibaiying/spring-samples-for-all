package com.heibaiying.springboot.consumer;

import com.alibaba.fastjson.JSON;
import com.heibaiying.springboot.bean.Programmer;
import com.heibaiying.springboot.constant.Topic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author : heibaiying
 * @description : kafka 消费者
 */

@Component
@Slf4j
public class KafkaBeanConsumer {

    @KafkaListener(groupId = "beanGroup", topics = Topic.BEAN)
    public void consumer(ConsumerRecord<String, Object> record) {
        System.out.println("消费者收到消息:" + JSON.parseObject(record.value().toString(), Programmer.class));
    }
}

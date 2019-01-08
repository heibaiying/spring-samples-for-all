package com.heibaiying.springboot.consumer;

import com.heibaiying.springboot.constant.Topic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author : heibaiying
 * @description : kafka 简单消息消费者
 */

@Component
@Slf4j
public class KafkaSimpleConsumer {

    // 简单消费者
    @KafkaListener(groupId = "simpleGroup", topics = Topic.SIMPLE)
    public void consumer1_1(ConsumerRecord<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,Consumer consumer) {
        System.out.println("消费者收到消息:" + record.value() + "; topic:" + topic);
        consumer.commitSync();
    }
}

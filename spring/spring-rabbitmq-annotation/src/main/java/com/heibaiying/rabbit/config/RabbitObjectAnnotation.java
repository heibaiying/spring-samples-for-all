package com.heibaiying.rabbit.config;

import com.heibaiying.constant.Type;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : heibaiying
 * @description : 声明队列、交换机、绑定关系、用于测试对象的消息传递
 */

@Configuration
public class RabbitObjectAnnotation {


    @Bean
    public DirectExchange objectTopic() {
        // 创建一个持久化的交换机
        return new DirectExchange("objectTopic", true, false);
    }

    @Bean
    public Queue objectQueue() {
        // 创建一个持久化的队列
        return new Queue("objectQueue", true);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(objectQueue()).to(objectTopic()).with("object");
    }


    /*创建队列消费者监听*/
    @Bean
    public SimpleMessageListenerContainer objectQueueLister(ConnectionFactory connectionFactory) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        // 设置监听的队列
        container.setQueues(objectQueue());
        // 将监听到的消息委派给实际的处理类
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        // 指定由哪个方法来处理消息 默认就是handleMessage
        adapter.setDefaultListenerMethod("handleMessage");

        // 消息转换
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        // 针对不同的消息体调用不同的重载方法
        idClassMapping.put(Type.MANAGER, com.heibaiying.bean.ProductManager.class);
        idClassMapping.put(Type.PROGRAMMER, com.heibaiying.bean.Programmer.class);

        javaTypeMapper.setIdClassMapping(idClassMapping);

        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        return container;
    }

}

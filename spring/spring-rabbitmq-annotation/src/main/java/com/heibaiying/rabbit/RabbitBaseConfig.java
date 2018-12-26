package com.heibaiying.rabbit;

import com.heibaiying.rabbit.config.RabbitProperty;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

/**
 * @author : heibaiying
 * @description : 声明队列、交换机、绑定关系、和队列消息监听
 */

@Configuration
@ComponentScan("com.heibaiying.rabbit.config")
public class RabbitBaseConfig {


    /**
     * 声明连接工厂
     */
    @Bean
    public ConnectionFactory connectionFactory(RabbitProperty property) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(property.getAddresses());
        connectionFactory.setUsername(property.getUsername());
        connectionFactory.setPassword(property.getPassword());
        connectionFactory.setVirtualHost(property.getVirtualhost());
        return connectionFactory;
    }

    /**
     * 创建一个管理器（org.springframework.amqp.rabbit.core.RabbitAdmin），用于管理交换，队列和绑定。
     *  auto-startup 指定是否自动声明上下文中的队列,交换和绑定, 默认值为true。
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }
}

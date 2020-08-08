package com.hobart.mq.rabbit.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * topic类型的exchange发送消息
 * 
 * 根据routingKey 规则投递到不到同的queue
 * 
 */
public class Procuder4TopicExchange {
    
    public static void main(String[] args) throws Exception{
        //1、创建连接工厂，并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.123.102");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("hubo");
        connectionFactory.setPassword("123456");
        
        //网络闪断 自动重试
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);
        

        //2、通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //3、创建通道
        Channel channel = connection.createChannel();

        //4、通过channel发送消息
        String exchangeName = "test_topic_exchange";
        String routingKey1 = "user.save";
        String routingKey2 = "user.update";
        String routingKey3 = "user.delete.abc";
        
        //当绑定的routingKey为user.# 可以收到3条消息
        //当绑定的routingKey为user.* 可以收到2条消息
        String message = "Hello World RabbitMQ for Topic Exchange Message...";
        //exchange 没设置会生成 一个默认exchange 匹配 队列和 routingkey一样的
        channel.basicPublish(exchangeName, routingKey1, null, message.getBytes());
        channel.basicPublish(exchangeName, routingKey2, null, message.getBytes());
        channel.basicPublish(exchangeName, routingKey3, null, message.getBytes());
        //5、记得关闭连接
        channel.close();
        connection.close();
    }
}

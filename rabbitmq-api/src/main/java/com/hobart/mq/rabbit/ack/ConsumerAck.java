package com.hobart.mq.rabbit.ack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 手动ack 和 重回队列 消费端
 */
public class ConsumerAck {

    public static void main(String[] args) throws Exception {
        //1、创建连接工厂，并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.123.102");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("hubo");
        connectionFactory.setPassword("123456");

        //2、通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //3、创建通道
        Channel channel = connection.createChannel();

        //通过channel消费消息
        //4、声明（创建）一个队列
        String exchangeName = "test_ack_exchange";
        String routingKey = "ack.#";
        String queueName = "test_ack_name";
        channel.exchangeDeclare(exchangeName,"topic",true,false,null);
        channel.queueDeclare(queueName,true,false,false,null);
        channel.queueBind(queueName,exchangeName,routingKey);
        //autoAck 自动签收  如果要限流qos那么要手动签收，设置成false 消费完成才ack,ack了mq才会发送消息到消费端
        channel.basicConsume(queueName,false, new MyConsumerAck(channel));
    }
}

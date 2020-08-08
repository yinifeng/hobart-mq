package com.hobart.mq.rabbit.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class ProcuderAck {

    public static void main(String[] args) throws IOException, TimeoutException {
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
        String exchangeName = "test_ack_exchange";
        String routingKey = "ack.save";
        //4、通过channel发送消息
        for (int i = 0 ; i < 5 ;i++) {
            String message = "Hello World RabbitMq ack " + i;
            Map<String,Object> headers  = new HashMap<>();
            headers.put("num", i);
            AMQP.BasicProperties properties  = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .headers(headers).build();
            //exchange 没设置会生成 一个默认exchange 匹配 队列和 routingkey一样的
            channel.basicPublish(exchangeName, routingKey, properties, message.getBytes()); 
        }
        //5、记得关闭连接
        channel.close();
        connection.close();
    }
}

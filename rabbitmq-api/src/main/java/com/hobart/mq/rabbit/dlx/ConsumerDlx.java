package com.hobart.mq.rabbit.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列的消费端
 */
public class ConsumerDlx {

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
        //4、声明一个普通的交换机和队列
        String exchangeName = "test_dlx_exchange";
        String routingKey = "dlx.save";
        String queueName = "test_dlx_name";
        channel.exchangeDeclare(exchangeName,"topic",true,false,null);
        
        //定义死信队列：
        String dlxExchange = "dlx.exchange";
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange",dlxExchange);
        channel.queueDeclare(queueName,true,false,false,arguments);
        
        //创建死信exchange并且绑定死信queue
        channel.exchangeDeclare(dlxExchange,"topic",true,false,null);
        channel.queueDeclare("dlx.queue",true,false,false,null);
        channel.queueBind("dlx.queue",dlxExchange,"#");
        
        channel.queueBind(queueName,exchangeName,routingKey);


        channel.basicConsume(queueName,true, new MyConsumerDlx(channel));
    }
}

package com.hobart.mq.rabbit.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 直连类型的exchange发送消息
 */
public class Procuder4DirectExchange {
    
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
        String exchangeName = "test_direct_exchange";
        String routingKey = "test.direct";
        
        String message = "Hello World RabbitMQ for Direct Exchange Message...";
        //exchange 没设置会生成 一个默认exchange 匹配 队列和 routingkey一样的
        channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
        //5、记得关闭连接
        channel.close();
        connection.close();
    }
}

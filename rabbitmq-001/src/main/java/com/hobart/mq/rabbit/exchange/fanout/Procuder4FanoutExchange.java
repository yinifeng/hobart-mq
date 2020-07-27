package com.hobart.mq.rabbit.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * fanout的exchange发送消息
 * 
 * 只发送到 exchange和queue绑定的队列，和routingkey没任何关系
 * 这种方式发送消息是性能最高的
 */
public class Procuder4FanoutExchange {
    
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
        String exchangeName = "test_fanout_exchange";
        
        String message = "Hello World RabbitMQ for Fanout Exchange Message...";
        //fanout的交换机 routingKey随便设置成什么都没关系
        for (int i = 0; i < 10; i++) {
            channel.basicPublish(exchangeName, "dsfsd", null, (message + i).getBytes());
        }
        
        //5、记得关闭连接
        channel.close();
        connection.close();
    }
}

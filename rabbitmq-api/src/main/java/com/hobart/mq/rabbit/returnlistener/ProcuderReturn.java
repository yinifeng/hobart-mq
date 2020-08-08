package com.hobart.mq.rabbit.returnlistener;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息返回模式投递  生产者
 */
public class ProcuderReturn {

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
        
        //指定消息的投递模式，确认模式
        channel.confirmSelect();
        
        String exchangeName = "test_return_exchange";
        String routingKey = "return.save";
        String errorRoutingKey = "abc.save";

        //4、通过channel发送消息
        String message  = "Hello RabbitMq send return message";
        //exchange 没设置会生成 一个默认exchange 匹配 队列和 routingkey一样的

        //添加一个返回监听
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("=========return message================");
                System.out.println("replyCode: " + replyCode);
                System.out.println("replyText: " + replyText);
                System.out.println("exchange: " + exchange);
                System.out.println("routingKey: " + routingKey);
                System.out.println("properties: " + properties);
                System.out.println("body: " + new String(body));
            }
        });
        //mandatory true 那么会处理返回监听
        channel.basicPublish(exchangeName, errorRoutingKey, true, null,message.getBytes());
        
       

        
        
        //5、不能关闭连接  不然不能收到监听回调
        //channel.close();
        //connection.close();
    }
}

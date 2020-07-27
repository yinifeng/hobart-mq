package com.hobart.mq.rabbit.message;

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
public class ProcuderMessage {

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
        
        Map<String,Object> headers = new HashMap<>();
        headers.put("my1","111");
        headers.put("my2","222");
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                //持久化策略 2 rabbitmq服务重启会存到磁盘
                .deliveryMode(2)
                //消息编码
                .contentEncoding("UTF-8")
                //过期时间 10s没消费自动删除
                .expiration("10000")
                //消息配置 消息扩展属性
                .headers(headers)
                .build();

        //4、通过channel发送消息
        for (int i = 0 ; i < 5 ;i++) {
            String message = "Hello RabbitMq" + i;
            //exchange 没设置会生成 一个默认exchange 匹配 队列和 routingkey一样的
            channel.basicPublish("", "test001", props, message.getBytes()); 
        }
        //5、记得关闭连接
        channel.close();
        connection.close();
    }
}

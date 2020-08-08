package com.hobart.mq.rabbit.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 死信队列
 *  1、channel.reject / channel.basicNack
 *  2、消息设置了过期时间未被消费，队列ttl时间未被消费的消息
 *  3、exchange路由到的queue已满
 * 以上3种情况都会进入死信队列
 * 进入死信队列就是重发到新的exchage
 *
 * 生产者
 */
public class ProcuderDlx {

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
        String exchangeName = "test_dlx_exchange";
        String routingKey = "dlx.save";
        //4、通过channel发送消息
        for (int i = 0 ; i < 1 ;i++) {
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    //持久化策略 2 rabbitmq服务重启会存到磁盘
                    .deliveryMode(2)
                    //消息编码
                    .contentEncoding("UTF-8")
                    //过期时间 10s没消费自动删除,还有针对队列的ttl
                    .expiration("10000")
                    .build();
            String message = "Hello World RabbitMq DLX " + i;
            //exchange 没设置会生成 一个默认exchange 匹配 队列和 routingkey一样的
            channel.basicPublish(exchangeName, routingKey, props, message.getBytes()); 
        }
        //5、记得关闭连接
        channel.close();
        connection.close();
    }
}

package com.hobart.mq.rabbit.confirmlistener;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息确认模式投递  生产者
 */
public class ProcuderConfirm {

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
        
        String exchangeName = "test_confirm_exchange";
        String routingKey = "confirm.save";

        //4、通过channel发送消息
        String message = "Hello RabbitMq send confirm message";
        //exchange 没设置会生成 一个默认exchange 匹配 队列和 routingkey一样的
        channel.basicPublish(exchangeName, routingKey, null, message.getBytes()); 
        
        //添加一个确认监听
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                //投递成功回调
                //deliveryTag 消息的唯一标签
                //multiple 是否批量的
                System.out.println("==========ACK================");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                //mq磁盘被写满了，mq服务异常了，队列达到容量上限了，都会触发noack
                //投递失败回调
                System.out.println("==========NO ACK================");
            }
        });
        
        
        //5、不能关闭连接  不然不能收到监听回调
        //channel.close();
        //connection.close();
    }
}

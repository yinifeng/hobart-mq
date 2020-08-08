package com.hobart.mq.rabbit.quickstart;

import com.hobart.mq.rabbit.QueueingConsumer;
import com.rabbitmq.client.*;

/**
 * 消费端
 */
public class Consumer {

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
        /**
         * queue 队列名称
         * durable 是否持久化队列
         * exclusive 是否独占 只有一个连接可以使用，保证消息消费的顺序性
         * autoDelete 是否自动删除，脱离了exchange，没有绑定关系 队列自动删除
         * arguments 扩展参数
         */
        String queueName = "test001";
        channel.queueDeclare(queueName,true,false,false,null);
        
        //5、创建消费者
        //DefaultConsumer consumer = new DefaultConsumer(channel);
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        
        //6、设置channel
        //autoAck 收到消息自动ack服务端
        channel.basicConsume(queueName,true,queueingConsumer);
        //channel.basicConsume(queueName, (consumerTag, delivery) -> {
        //    String msg = new String(delivery.getBody());
        //    System.out.println("消费端收到消息：" + msg);
        //    Envelope envelope = delivery.getEnvelope();
        //}, consumerTag -> {
        //});
        
        //7、获取消息
        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("消费端收到消息：" + msg);
            //处理消息唯一
            Envelope envelope = delivery.getEnvelope();
        }
        
        
        //5、记得关闭连接
        //channel.close();
        //connection.close();
    }
}

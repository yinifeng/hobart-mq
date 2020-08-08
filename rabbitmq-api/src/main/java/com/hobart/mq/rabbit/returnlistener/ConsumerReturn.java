package com.hobart.mq.rabbit.returnlistener;

import com.hobart.mq.rabbit.QueueingConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 消费端
 */
public class ConsumerReturn {

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
        //4、声明（创建）一个队列 并且绑定到exchange上
        String exchangeName = "test_return_exchange";
        String routingKey = "return.#";
        String queueName = "test_return_queue";
        //声明一个交换机 exchange
        channel.exchangeDeclare(exchangeName,"topic",true,false,false,null);
        //声明一个队列
        channel.queueDeclare(queueName,true,false,false,null);
        //绑定交换机和队列
        channel.queueBind(queueName,exchangeName,routingKey);
        
        //5、创建消费者
        //DefaultConsumer consumer = new DefaultConsumer(channel);
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        
        //6、设置channel
        //autoAck 收到消息自动ack服务端
        channel.basicConsume(queueName,true,queueingConsumer);
        
        //7、获取消息
        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("消费端收到消息：" + msg);
            //处理消息唯一
            //Envelope envelope = delivery.getEnvelope();
        }
        
        
        //5、记得关闭连接
        //channel.close();
        //connection.close();
    }
}

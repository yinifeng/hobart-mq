package com.hobart.mq.rabbit.exchange.fanout;

import com.hobart.mq.rabbit.QueueingConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;

/**
 * 直连类型的exchange消费消息
 * 
 * Name:交换机名称
 *
 * Type:交换机类型 direct(直连 routingkey 和 queue要一样)、topic、fanout、headers
 *
 * Durability:是否需要持久化，true为持久化
 *
 * Auto delete:自动删除，当最后一个绑定的queue删除后，exchange也会被自动删除
 *
 * Internal:当前Exchange是否用于RabbitMQ内部使用，默认为false
 *
 * Arguments:扩展参数
 */
public class Consumer4FanoutExchange {
    
    public static void main(String[] args) throws Exception{
        //1、创建连接工厂，并进行配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.123.102");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("hubo");
        connectionFactory.setPassword("123456");
        //网络闪断 自动重连
        connectionFactory.setAutomaticRecoveryEnabled(true);
        //每隔3秒重连
        connectionFactory.setNetworkRecoveryInterval(3000);

        //2、通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //3、创建通道
        Channel channel = connection.createChannel();

        //通过channel消费消息
        //4、声明
        String exchangeName = "test_fanout_exchange";
        String exchangeType = "fanout";
        //String routingKey = "";
        String queueName = "test_fanout_queue";
        //声明一个交换机 exchange
        channel.exchangeDeclare(exchangeName,exchangeType,true,false,false,null);
        //声明一个队列
        channel.queueDeclare(queueName,false,false,false,null);
        //绑定交换机和队列
        //fnaout的exchange 的routingkey不要设置，和routingkey没任何关系
        channel.queueBind(queueName,exchangeName,"");

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
            System.out.println("fanout消费端收到消息：" + msg);
            //处理消息唯一
            Envelope envelope = delivery.getEnvelope();
        }
    }
}

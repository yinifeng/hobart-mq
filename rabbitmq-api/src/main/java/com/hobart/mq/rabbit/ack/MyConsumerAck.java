package com.hobart.mq.rabbit.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class MyConsumerAck extends DefaultConsumer {

    public MyConsumerAck(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body)
            throws IOException
    {
        System.out.println("=============consumer message==============");
        //System.out.println("consumerTag: " + consumerTag);
        System.out.println("envelope: " + envelope);
        System.out.println("properties: " + properties);
        System.out.println("body: " + new String(body));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //当关掉自动ack那么这个地方不ack，那么mq就不会再发消息了
        //multiple false prefetchCount=1不是批量的消息 prefetchCount>1是批量消息
        Integer num = (Integer) properties.getHeaders().get("num");
        if (num == 0) {
            //No ack
            //requeue 是否重回队列
            this.getChannel().basicNack(envelope.getDeliveryTag(),false,true);
        } else {
            //ack
            this.getChannel().basicAck(envelope.getDeliveryTag(),false);
        }
    }
}

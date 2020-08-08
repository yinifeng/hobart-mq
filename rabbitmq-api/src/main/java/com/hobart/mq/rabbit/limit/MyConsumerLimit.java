package com.hobart.mq.rabbit.limit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class MyConsumerLimit extends DefaultConsumer {

    public MyConsumerLimit(Channel channel) {
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
        System.out.println("consumerTag: " + consumerTag);
        System.out.println("envelope: " + envelope);
        System.out.println("properties: " + properties);
        System.out.println("body: " + new String(body));
        
        //当关掉自动ack那么这个地方不ack，那么mq就不会再发消息了
        //multiple false prefetchCount=1不是批量的消息 prefetchCount>1是批量消息
        this.getChannel().basicAck(envelope.getDeliveryTag(),false);
    }
}

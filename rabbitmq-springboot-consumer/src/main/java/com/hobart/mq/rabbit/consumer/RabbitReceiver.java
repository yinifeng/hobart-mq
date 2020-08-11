package com.hobart.mq.rabbit.consumer;

import com.hobart.mq.rabbit.domain.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class RabbitReceiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue-1", durable = "true"),
            exchange = @Exchange(value = "exchange-1",
                    durable = "true", type = "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "springboot.*")
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws IOException {
        System.err.println("-----------消费端消费消息----------------");
        System.err.println("Payload: "+message.getPayload());
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        System.err.println("DeliveryTag: "+deliveryTag);
        //手动ack
        channel.basicAck(deliveryTag,false);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${my.listener.order.queue.name}", 
                    durable = "${my.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${my.listener.order.exchange.name}",
                    durable = "${my.listener.order.exchange.durable}", type = "${my.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${my.listener.order.exchange.ignoreDeclarationExceptions}"),
            key = "${my.listener.order.routingkey}")
    )
    @RabbitHandler
    public void onOrderMessage(@Payload Order order, @Headers Map<String,Object> heards,Channel channel) throws IOException {
        System.err.println("-----------消费端消费Order消息----------------");
        System.err.println("Order: "+order);
        Long deliveryTag = (Long) heards.get(AmqpHeaders.DELIVERY_TAG);
        System.err.println("DeliveryTag: "+deliveryTag);
        //手动ack
        channel.basicAck(deliveryTag,false);
    }
}

package com.hobart.mq.rabbit.producer;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback(){
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.err.println("----------------Confirm Callback-------------------");
            System.err.println("correlationData: " + correlationData);
            System.err.println("ack: " + ack);
            System.err.println("cause: " + cause);
            if (!ack) {
                System.err.println("发送RabbitMQ异常.....");
            }else {
                //ack 实际业务确认成功可以更新消息投递状态
            }
        }
    };
    
    final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("----------------Return Callback-------------------");
            System.err.println("message: " + message);
            System.err.println("replyCode: " + replyCode);
            System.err.println("replyText: " + replyText);
            System.err.println("exchange: " + exchange);
            System.err.println("routingKey: " + routingKey);
        }
    };
    
    public void send(Object message, Map<String,Object> properties) throws Exception{
        MessageHeaders mhs = new MessageHeaders(properties);
        Message<Object> msg = MessageBuilder.createMessage(message, mhs);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        
        //标识全局唯一消息，实际业务开发很重要区分消息全局唯一性
        //业务id + 时间戳
        CorrelationData correlationData = new CorrelationData("1234567890");
        
        rabbitTemplate.convertAndSend("exchange-1", "spring.hello",msg,correlationData);
    }
}

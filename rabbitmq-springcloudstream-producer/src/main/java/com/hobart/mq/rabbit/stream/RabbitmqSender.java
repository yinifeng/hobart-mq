package com.hobart.mq.rabbit.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@EnableBinding(Barista.class)
@Component
public class RabbitmqSender {
    @Autowired
    private Barista barista;
    
    //发送消息
    public String sendMessage(Object message, Map<String,Object> properties) throws Exception{
        try {
            MessageHeaders mhs = new MessageHeaders(properties);
            Message<Object> msg = MessageBuilder.createMessage(message, mhs);
            boolean sendStatus = barista.logoutput().send(msg);
            System.err.println("-------------sending----------------");
            System.out.println("发送数据:"+message+",sendStatus: "+sendStatus);
        } catch (Exception e) {
            System.err.println("-------------error----------------");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }
}
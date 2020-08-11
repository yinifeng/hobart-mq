package com.hobart.mq.rabbit.stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqSenderTest {
    @Autowired
    private RabbitmqSender rabbitmqSender;
    
    @Test
    public void sendMessageTest1(){
        for (int i = 0; i<1;i++){
            try {
                Map<String,Object> properties = new HashMap<>();
                properties.put("SERIAL_NUMBER","12345");
                properties.put("BANK_NUMBER","abc");
                properties.put("PLAT_SEND_TIME",new Timestamp(System.currentTimeMillis()));
                rabbitmqSender.sendMessage("Hello SpringCloud Strem,I am amqp send num "+i,properties);
            } catch (Exception e) {
                System.err.println("----------------error----------------");
                e.printStackTrace();
            }
        }
    }

}
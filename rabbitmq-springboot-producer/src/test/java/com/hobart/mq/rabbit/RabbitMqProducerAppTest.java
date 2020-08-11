package com.hobart.mq.rabbit;

import com.hobart.mq.rabbit.domain.Order;
import com.hobart.mq.rabbit.producer.RabbitSender;
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
public class RabbitMqProducerAppTest {
    @Autowired
    private RabbitSender rabbitSender;
    
    @Test
    public void testSend1() throws Exception{
        Map<String, Object> properties= new HashMap<>();
        properties.put("number","123456");
        properties.put("send_time",new Timestamp(System.currentTimeMillis()).toString());
        rabbitSender.send("Hello RabbitMQ For SpringBoot",properties);
    }
    
    @Test
    public void testSend2() throws Exception{
        testSend1();
        
        Order order = new Order("1001","第一个订单");
        rabbitSender.sendOrder(order);
    }
}
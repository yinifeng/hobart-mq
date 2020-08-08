package com.hobart.mq.rabbit.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * 使用junit进行单元测试
 */
//指定在单元测试启动的时候创建spring的工厂类对象
@ContextConfiguration(/*locations = {"classpath:applicationContext.xml"}*/
        classes = RabbitMqConfiguration.class
)
//RunWith的value属性指定以spring test的SpringJUnit4ClassRunner作为启动类
//如果不指定启动类，默认启用的junit中的默认启动类
@RunWith(value = SpringJUnit4ClassRunner.class)
public class RabbitMqConfigurationTest {
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @Test
    public void testRabbitAdmin() {
        rabbitAdmin.declareExchange(new DirectExchange("test.spring.direct",false,false));
        rabbitAdmin.declareExchange(new TopicExchange("test.spring.topic",false,false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.spring.fanout",false,false));
    }
}
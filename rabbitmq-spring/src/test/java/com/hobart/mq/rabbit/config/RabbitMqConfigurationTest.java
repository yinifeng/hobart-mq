package com.hobart.mq.rabbit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hobart.mq.rabbit.domain.Order;
import com.hobart.mq.rabbit.domain.Packaged;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Or;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

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
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testRabbitAdmin() {
        rabbitAdmin.declareExchange(new DirectExchange("test.spring.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("test.spring.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.spring.fanout", false, false));

        rabbitAdmin.declareQueue(new Queue("test.spring.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.spring.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.spring.fanout.queue", false));

        //Binding.DestinationType.QUEUE 绑定队列
        rabbitAdmin.declareBinding(new Binding("test.spring.direct.queue", 
                Binding.DestinationType.QUEUE, "test.spring.direct", 
                "spring.direct", new HashMap<>()));
        
        //一步到位
        rabbitAdmin.declareBinding(BindingBuilder
                .bind(new Queue("test.spring.topic.queue", false))
                .to(new TopicExchange("test.spring.topic", false, false))
                .with("test.spring.topic")
        );
        
        //直连不需要指定路由键了
        rabbitAdmin.declareBinding(BindingBuilder
                .bind(new Queue("test.spring.fanout.queue", false))
                .to(new FanoutExchange("test.spring.fanout", false, false))
        );
        
        //清空队列数据
        rabbitAdmin.purgeQueue("test.spring.topic.queue",false);
    }
    
    @Test
    public void testSendMessage() throws Exception{
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc","自定义描述");
        messageProperties.getHeaders().put("type","自定义消息类型");
        
        Message message = new Message("Hello Spring Rabbit".getBytes(), messageProperties);
        this.rabbitTemplate.convertAndSend("topic001","spring.amqp",message,new MessagePostProcessor(){
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties msgProperties = message.getMessageProperties();
                msgProperties.getHeaders().put("desc","修改的描述信息");
                msgProperties.getHeaders().put("attr","新增信息");
                return message;
            }
        });
    }

    /**
     * RabbitMqConfiguration#messageContainer 1 测试 
     * 自定义转换器 测试
     * @throws Exception
     */
    @Test
    public void testSendMessage2() throws Exception{
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("Hello Spring Rabbit".getBytes(), messageProperties);
        this.rabbitTemplate.send("topic001","spring.amqp",message);

        this.rabbitTemplate.convertAndSend("topic001","spring.abcd","Hello Spring Rabbit2");
        this.rabbitTemplate.convertAndSend("topic002","rabbit.abcd","Hello Spring Rabbit2");
    }

    /**
     * RabbitMqConfiguration#messageContainer 2测试 
     * @throws Exception
     */
    @Test
    public void testSendMessage4Text() throws Exception{
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message1 = new Message("Hello Spring Rabbit 001".getBytes(), messageProperties);
        Message message2 = new Message("Hello Spring Rabbit 002".getBytes(), messageProperties);
        this.rabbitTemplate.send("topic001","spring.amqp",message1);
        this.rabbitTemplate.send("topic002","rabbit.amqp",message2);
    }

    /**
     * RabbitMqConfiguration#messageContainer 3.1测试 
     * @throws Exception
     */
    @Test
    public void testSendMessage4Json() throws Exception{
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Order order = new Order();
        order.setId(1001);
        order.setName("消息订单");
        order.setContent("订单内容");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(order);
        System.err.println("订单转换成的json: " + jsonStr);
        Message message1 = new Message(jsonStr.getBytes(), messageProperties);
        this.rabbitTemplate.send("topic001","spring.json",message1);
    }

    /**
     * RabbitMqConfiguration#messageContainer 3.2测试 
     * @throws Exception
     */
    @Test
    public void testSendMessage4JavaType() throws Exception{
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        //设置转换头类型
        messageProperties.getHeaders().put("__TypeId__","com.hobart.mq.rabbit.domain.Order");
        Order order = new Order();
        order.setId(1002);
        order.setName("消息订单");
        order.setContent("订单内容");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(order);
        System.err.println("订单转换成的json: " + jsonStr);
        Message message1 = new Message(jsonStr.getBytes(), messageProperties);
        this.rabbitTemplate.send("topic001","spring.order",message1);
    }

    /**
     * RabbitMqConfiguration#messageContainer 3.3测试 
     * @throws Exception
     */
    @Test
    public void testSendMessage4JavaType2() throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Order order = new Order();
        order.setId(1002);
        order.setName("消息订单");
        order.setContent("订单内容");
        String jsonStr1 = objectMapper.writeValueAsString(order);
        System.err.println("订单转换成的json: " + jsonStr1);

        Packaged packaged = new Packaged();
        packaged.setId(2001);
        packaged.setName("消息packaged");
        packaged.setDesc("packaged内容");
        String jsonStr2 = objectMapper.writeValueAsString(order);
        System.err.println("packaged转换成的json: " + jsonStr2);
        
        MessageProperties messageProperties1 = new MessageProperties();
        messageProperties1.setContentType("application/json");
        //设置转换头类型
        messageProperties1.getHeaders().put("__TypeId__","order");

        MessageProperties messageProperties2 = new MessageProperties();
        messageProperties2.setContentType("application/json");
        //设置转换头类型
        messageProperties2.getHeaders().put("__TypeId__","packaged");

        
        
        Message message1 = new Message(jsonStr1.getBytes(), messageProperties1);
        Message message2 = new Message(jsonStr2.getBytes(), messageProperties2);
        this.rabbitTemplate.send("topic001","spring.order",message1);
        this.rabbitTemplate.send("topic002","rabbit.packed",message2);
    }

    /**
     * RabbitMqConfiguration#messageContainer 3.4测试 
     * @throws Exception
     */
    @Test
    public void testSendMessage4Pdf() throws Exception{
        byte[] body = Files.readAllBytes(Paths.get("d:/test", "436_7722_cn.pdf"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/pdf");
        Message message1 = new Message(body, messageProperties);
        this.rabbitTemplate.send("","pdf_queue",message1);
    }

    /**
     * RabbitMqConfiguration#messageContainer 3.4测试 
     * @throws Exception
     */
    @Test
    public void testSendMessage4Image() throws Exception{
        byte[] body = Files.readAllBytes(Paths.get("d:/test", "sendmsg.png"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("image/png");
        messageProperties.getHeaders().put("extName","png");
        Message message1 = new Message(body, messageProperties);
        this.rabbitTemplate.send("","image_queue",message1);
    }

}
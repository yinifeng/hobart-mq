package com.hobart.mq.rabbit.config;
import com.hobart.mq.rabbit.adapter.MyMessageAdapter;
import com.hobart.mq.rabbit.converter.ImageMessageConverter;
import com.hobart.mq.rabbit.converter.PdfMessageConverter;
import com.hobart.mq.rabbit.converter.TextMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class RabbitMqConfiguration {
    
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername("hubo");
        connectionFactory.setPassword("123456");
        connectionFactory.setHost("192.168.123.102");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //设置true，spring容器启动rabbitmq才会启动连接
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
    
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true,false);
    }
    
    @Bean
    public Queue queue001() {
        return new Queue("queue001",true);//持久化队列
    }
    
    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true,false);
    }

    @Bean
    public Queue queue002() {
        return new Queue("queue002",true);//持久化队列
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        return new Queue("queue003",true);//持久化队列
    }

    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image() {
        return new Queue("image_queue",true);//持久化队列
    }

    @Bean
    public Queue queue_pdf() {
        return new Queue("pdf_queue",true);//持久化队列
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //rabbitTemplate.set
        return rabbitTemplate;
    }
    
    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //监听的队列
        container.setQueues(queue001(),queue002(),queue003(),queue_image(),queue_pdf());
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        //拒绝重回队列
        container.setDefaultRequeueRejected(false);
        //自动ack
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setExposeListenerChannel(true);
        //消费标签的自定义
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString().replaceAll("-", "");
            }
        });
        //自定义消费者
        /*
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                String msg = new String(message.getBody());
                System.err.println("---------消费者: " + msg);
            }
        });
         */
        
        /*
        //1、默认适配方式：方法名称
        MyMessageAdapter delegate = new MyMessageAdapter();
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(delegate);
        //设置 delegate消费方法 入参一定是byte[]数组 默认方法handleMessage
        messageListenerAdapter.setDefaultListenerMethod("consumerMessage");
        //消息转换
        messageListenerAdapter.setMessageConverter(new TextMessageConverter());
        //消费者的适配器
        container.setMessageListener(messageListenerAdapter);
         */
        
        /* 
        //2、队列名称适配处理方法
        MyMessageAdapter delegate = new MyMessageAdapter();
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(delegate);
        Map<String,String> queueOrTagToMethodName = new HashMap<>();
        queueOrTagToMethodName.put("queue001","method1");
        queueOrTagToMethodName.put("queue002","method2");
        messageListenerAdapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
        //消息转换
        messageListenerAdapter.setMessageConverter(new MyMessageConverter());
        //消费者的适配器
        container.setMessageListener(messageListenerAdapter);
        */
        
        /*
        //3.1支持json格式的转换器
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MyMessageAdapter());
        messageListenerAdapter.setDefaultListenerMethod("consumerJsonMessage");
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        messageListenerAdapter.setMessageConverter(jsonMessageConverter);
        //消费者的适配器
        container.setMessageListener(messageListenerAdapter);
         */
        
        /*
        //3.2支持json格式的转换器,同时消费的信息转换成java对象
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MyMessageAdapter());
        messageListenerAdapter.setDefaultListenerMethod("consumerMessage");
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        //添加实体类扫描的包名
        javaTypeMapper.addTrustedPackages("com.hobart.mq.rabbit.domain");
        jsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        messageListenerAdapter.setMessageConverter(jsonMessageConverter);
        //消费者的适配器
        container.setMessageListener(messageListenerAdapter);
         */
        
        /*
        //3.3 支持json格式的转换器,同时消费的信息转换成java对象,支持多类型适配到不同的方法
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MyMessageAdapter());
        messageListenerAdapter.setDefaultListenerMethod("consumerMessage");
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        //添加实体类扫描的包名
        //javaTypeMapper.addTrustedPackages("com.hobart.mq.rabbit.domain");
        //jsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("order", Order.class);
        idClassMapping.put("packaged", Packaged.class);
        javaTypeMapper.setIdClassMapping(idClassMapping);
        jsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        messageListenerAdapter.setMessageConverter(jsonMessageConverter);
        //消费者的适配器
        container.setMessageListener(messageListenerAdapter);
         */
        
        
        //3.4 组合多种类型转换器
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MyMessageAdapter());
        messageListenerAdapter.setDefaultListenerMethod("consumerMessage");
        ContentTypeDelegatingMessageConverter messageConverter = new ContentTypeDelegatingMessageConverter();
        TextMessageConverter textMessageConverter = new TextMessageConverter();
        messageConverter.addDelegate("text",textMessageConverter);
        messageConverter.addDelegate("html/text",textMessageConverter);
        messageConverter.addDelegate("xml/text",textMessageConverter);
        messageConverter.addDelegate("text/plain",textMessageConverter);
        
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        messageConverter.addDelegate("json",jsonMessageConverter);
        messageConverter.addDelegate("application/json",jsonMessageConverter);

        ImageMessageConverter imageMessageConverter = new ImageMessageConverter();
        messageConverter.addDelegate("image",imageMessageConverter);
        messageConverter.addDelegate("image/png",imageMessageConverter);

        PdfMessageConverter pdfMessageConverter = new PdfMessageConverter();
        messageConverter.addDelegate("application/pdf",pdfMessageConverter);

        messageListenerAdapter.setMessageConverter(messageConverter);
        //消费者的适配器
        container.setMessageListener(messageListenerAdapter);
        return container;
    }
}

package com.hobart.mq.rabbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitMqConsumerApp {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMqConsumerApp.class, args);
    }
}

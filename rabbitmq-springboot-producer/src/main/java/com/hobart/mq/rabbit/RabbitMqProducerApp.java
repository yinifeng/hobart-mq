package com.hobart.mq.rabbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitMqProducerApp {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMqProducerApp.class,args);
    }
}

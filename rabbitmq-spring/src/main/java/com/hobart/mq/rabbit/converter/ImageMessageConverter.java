package com.hobart.mq.rabbit.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

public class ImageMessageConverter implements MessageConverter {
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        throw new MessageConversionException("image不支持");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        System.err.println("-----------Image MessageConverter-------------");
        Object extName = message.getMessageProperties().getHeaders().get("extName");
        String typeName = extName == null ? "png" : extName.toString();
        byte[] body = message.getBody();
        String fileId = UUID.randomUUID().toString().replaceAll("-", "");
        String filePath = "d:/test/mq/" + fileId + "." + typeName;
        File file = new File(filePath);
        try {
            Files.copy(new ByteArrayInputStream(body),file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}

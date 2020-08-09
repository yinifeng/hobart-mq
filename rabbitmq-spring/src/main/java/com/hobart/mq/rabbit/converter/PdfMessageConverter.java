package com.hobart.mq.rabbit.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

public class PdfMessageConverter implements MessageConverter {
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        throw new MessageConversionException("pdf不支持");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        System.err.println("-----------Pdf MessageConverter-------------");
        String fileId = UUID.randomUUID().toString().replaceAll("-", "");
        String filePath = "d:/test/mq/" + fileId + ".pdf";
        File file = new File(filePath);
        try {
            Files.copy(new ByteArrayInputStream(message.getBody()),file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}

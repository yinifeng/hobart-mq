package com.hobart.mq.rabbit.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface Barista {

    String OUTPUT_CHANNEL = "output_channel";

    //声明一个输出类的通道
    @Output(Barista.OUTPUT_CHANNEL)
    MessageChannel logoutput();
}

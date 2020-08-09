package com.hobart.mq.rabbit.adapter;

import com.hobart.mq.rabbit.domain.Order;
import com.hobart.mq.rabbit.domain.Packaged;

import java.io.File;
import java.util.Map;

public class MyMessageAdapter {
    
    //默认的处理方法
    public void handleMessage(byte[] body) {
        System.err.println("默认的处理方法: " + new String(body));
    }
    

    public void consumerMessage(byte[] body) {
        System.err.println("字节数组处理方法: " + new String(body));
    }

    public void consumerMessage(String body) {
        System.err.println("字符串,处理方法: " + body);
    }

    public void method1(String body) {
        System.err.println("method1处理queue001方法: " + body);
    }

    public void method2(String body) {
        System.err.println("method2处理queue002方法: " + body);
    }
    
    public void consumerMessage(Map<String,Object> jsonBody) {
        System.err.println("json转换处理方法: " + jsonBody);
    }
    
    public void consumerMessage(Order order) {
        System.err.println("json转换Javad类型Order处理方法: " + order);
    }

    public void consumerMessage(Packaged packaged) {
        System.err.println("json转换Java类型Packaged处理方法: " + packaged);
    }

    public void consumerMessage(File file) {
        System.err.println("文件流消息转换成文件: " + file.getName());
    }
}

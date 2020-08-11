package com.hobart.mq.rabbit.domain;

import java.io.Serializable;

public class Order implements Serializable {
    private static final long serialVersionUID = -1L;
    
    private String no;
    
    private String desc;

    public Order() {
    }

    public Order(String no, String desc) {
        this.no = no;
        this.desc = desc;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Order{" +
                "no='" + no + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}

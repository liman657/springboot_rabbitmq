package com.learn.springbootrabbitmqlearn.listener;

import org.springframework.context.ApplicationEvent;

/**
 * autor:liman
 * createtime:2019/10/20
 * comment:
 */
public class InsertOrderRecordEvent extends ApplicationEvent {

    private String orderNo;
    private String orderType;

    public InsertOrderRecordEvent(Object source,String orderNo,String orderType) {
        super(source);
        this.orderNo = orderNo;
        this.orderType = orderType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "InsertOrderRecordEvent{" +
                "orderNo='" + orderNo + '\'' +
                ", orderType='" + orderType + '\'' +
                '}';
    }
}

package com.learn.springbootrabbitmqlearn.dto;

import java.io.Serializable;

/**
 * autor:liman
 * createtime:2019/10/28
 * comment:
 */
public class LogDto implements Serializable {

    private String methodName;
    private String operateData;

    public LogDto() {
    }

    public LogDto(String methodName, String operateData) {
        this.methodName = methodName;
        this.operateData = operateData;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getOperateData() {
        return operateData;
    }

    public void setOperateData(String operateData) {
        this.operateData = operateData;
    }

    @Override
    public String toString() {
        return "LogDto{" +
                "methodName='" + methodName + '\'' +
                ", operateData='" + operateData + '\'' +
                '}';
    }
}

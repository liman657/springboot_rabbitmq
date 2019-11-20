package com.learn.springbootrabbitmqlearn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.springbootrabbitmqlearn.response.BaseResponse;
import com.learn.springbootrabbitmqlearn.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * autor:liman
 * createtime:2019/10/31
 * comment:
 * 延迟队列的controller
 */
@RestController
public class DelayQueueController {
    private static final Logger log= LoggerFactory.getLogger(DelayQueueController.class);
    private static final String Prefix="delay/queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    @RequestMapping(value = Prefix+"/send",method = RequestMethod.GET)
    public BaseResponse sendMail(@RequestParam String message){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            rabbitTemplate.setExchange(env.getProperty("simple.produce.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("simple.produce.routing.key.name"));
//            String str="延迟队列的消息";
            Message msg=MessageBuilder.withBody(objectMapper.writeValueAsBytes(message)).build();
            rabbitTemplate.convertAndSend(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("发送消息完毕----");
        return response;
    }
}

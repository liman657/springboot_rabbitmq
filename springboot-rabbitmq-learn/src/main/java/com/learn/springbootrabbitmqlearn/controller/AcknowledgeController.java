package com.learn.springbootrabbitmqlearn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.springbootrabbitmqlearn.dto.User;
import com.learn.springbootrabbitmqlearn.response.BaseResponse;
import com.learn.springbootrabbitmqlearn.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * autor:liman
 * createtime:2019/10/26
 * comment:
 */
@RestController
public class AcknowledgeController {

    private static final Logger log  = LoggerFactory.getLogger(AcknowledgeController.class);

    private static final String Prefix = "ack";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    @RequestMapping(value = Prefix+"/user/info",method = RequestMethod.GET)
    public BaseResponse ackUser(){
        User user = new User(1,"liman","zouhuifang");
        try {
//            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//            rabbitTemplate.setExchange(env.getProperty("simple.mq.exchange.name"));
//            rabbitTemplate.setRoutingKey(env.getProperty("simple.mq.routing.key.name"));
//
//            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(user))
//                    .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
//                    .build();
//
//            rabbitTemplate.convertAndSend(message);
            for(int i=0;i<100;i++){
                new Thread(new testMultiThreadMessage(rabbitTemplate,user)).start();
            }
        }catch (Exception e){
            log.error("发送消息异常,{}",e.fillInStackTrace());
        }
        return new BaseResponse(StatusCode.Success);
    }

    private class testMultiThreadMessage implements Runnable{

        private RabbitTemplate rabbitTemplate;
        private User user;

        public testMultiThreadMessage(RabbitTemplate rabbitTemplate,User user) {
            this.rabbitTemplate = rabbitTemplate;
            this.user = user;
        }

        @Override
        public void run() {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("simple.mq.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("simple.mq.routing.key.name"));

            Message message = null;
            try {
                message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(user))
                        .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
                        .build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            rabbitTemplate.convertAndSend(message);
        }
    }

}

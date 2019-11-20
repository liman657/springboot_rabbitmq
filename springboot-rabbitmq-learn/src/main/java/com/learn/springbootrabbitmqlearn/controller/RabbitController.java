package com.learn.springbootrabbitmqlearn.controller;

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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * autor:liman
 * createtime:2019/10/20
 * comment:
 */
@RestController
public class RabbitController {

    private static final Logger log= LoggerFactory.getLogger(RabbitController.class);

    private static final String Prefix="rabbit";

    @Autowired
    private Environment env;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

//    @Autowired
//    private Gson gson;

    /**
     * 发送简单消息
     * @param message
     * @return
     */
    @RequestMapping(value = Prefix+"/simple/message/send",method = RequestMethod.GET)
    public BaseResponse sendSimpleMessage(@RequestParam String message){
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try{
            log.info("待发送消息：{}",message);

            rabbitTemplate.setExchange(env.getProperty("spring.rabbitmq.learn.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("spring.rabbitmq.learn.routing.key.name"));
//            Message msg=MessageBuilder.withBody(message.getBytes("UTF-8")).build();
//            gson.

            Message msg=MessageBuilder.withBody(objectMapper.writeValueAsBytes(message)).build();
//            Message msg=MessageBuilder.withBody(gson.toJson(message).getBytes("UTF-8")).build();
            rabbitTemplate.send(msg);
//            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        }catch (Exception e){

        }
        return response;
    }

    //,consumes = MediaType.APPLICATION_JSON_VALUE
    @RequestMapping(value=Prefix+"/object/message/send",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse sendObjectMessage(@RequestBody User user){
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);
        try{
            log.info("待发送的消息：{}",user);
            rabbitTemplate.setExchange(env.getProperty("spring.rabbitmq.learn.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("spring.rabbitmq.learn.routing.key.name"));
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(user))
                    .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT).build();

//            Message msg=MessageBuilder.withBody(gson.toJson(user).getBytes("UTF-8")).build();
            rabbitTemplate.send(message);
            log.info("需要发送的消息为：{}",message);
            rabbitTemplate.convertAndSend(message);
        }catch (Exception e){
            log.error("发送对象消息发生异常：{}",e.fillInStackTrace());
        }
        return baseResponse;
    }
}

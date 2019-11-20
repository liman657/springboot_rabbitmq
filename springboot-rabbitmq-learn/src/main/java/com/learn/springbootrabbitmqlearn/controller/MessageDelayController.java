package com.learn.springbootrabbitmqlearn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.springbootrabbitmqlearn.dto.LogDto;
import com.learn.springbootrabbitmqlearn.dto.UserOrderDto;
import com.learn.springbootrabbitmqlearn.entity.UserOrder;
import com.learn.springbootrabbitmqlearn.mapper.UserOrderMapper;
import com.learn.springbootrabbitmqlearn.response.BaseResponse;
import com.learn.springbootrabbitmqlearn.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * autor:liman
 * createtime:2019/10/27
 * comment:消息级别的延时配置（非消息队列）
 */
@RestController
public class MessageDelayController {

    private static final Logger log = LoggerFactory.getLogger(MessageDelayController.class);

    private static final String Prefix = "message/delay";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    /**
     * 延时队列下订单
     * @param
     * @return
     */
    @RequestMapping(value=Prefix+"/messageTTL",method = RequestMethod.GET)
    public BaseResponse pushOrder2Delay(@RequestParam  Integer flag){
        BaseResponse response = new BaseResponse(StatusCode.Success);
        long ttl = 10000l;
        if(1==flag){
            ttl=5000l;
        }
        String msg = "消息级别的延时实例,延时长度为:"+ttl;
        log.info("发送的消息为:{},时间为:{}",msg,new Date());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(env.getProperty("user.message.produce.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("user.message.produce.route.key"));
        long finalTtl = ttl;
        rabbitTemplate.convertAndSend(java.util.Optional.ofNullable(msg), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties properties=message.getMessageProperties();
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,String.class);

                properties.setExpiration(String.valueOf(finalTtl));
                return message;
            }
        });
        return response;
    }

}
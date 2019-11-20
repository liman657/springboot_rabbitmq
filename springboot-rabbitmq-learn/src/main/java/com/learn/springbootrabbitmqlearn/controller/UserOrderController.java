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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * autor:liman
 * createtime:2019/10/27
 * comment:
 */
@RestController
public class UserOrderController {

    private static final Logger log = LoggerFactory.getLogger(UserOrderController.class);

    private static final String Prefix = "user/order";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserOrderMapper userOrderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    @RequestMapping(value=Prefix+"/push",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse pushOrder(@RequestBody UserOrderDto userOrderDto){
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            log.error("接收到数据:{}",userOrderDto);

            rabbitTemplate.setExchange(env.getProperty("user.order.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("user.order.routing.key.name"));
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(userOrderDto))
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            rabbitTemplate.convertAndSend(message);
        }catch (Exception e){
            log.error("出现异常:{}",e.fillInStackTrace());
        }

        //异步写日志
        try{
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("log.system.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("log.system.routing.key.name"));

            LogDto logDto = new LogDto("pushUserOrder",objectMapper.writeValueAsString(userOrderDto));
            rabbitTemplate.convertAndSend(logDto, new MessagePostProcessor() {//初始化一个消息后置处理
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties properties = message.getMessageProperties();
                    properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    properties.setHeader(AbstractJavaTypeMapper.DEFAULT_KEY_CLASSID_FIELD_NAME,LogDto.class);
                    return message;
                }
            });

            log.info("主线程继续");
        }catch (Exception e){
            log.error("异常:{}",e.fillInStackTrace());
        }
        return response;
    }

    /**
     * 延时队列下订单
     * @param userOrderDto
     * @return
     */
    @RequestMapping(value=Prefix+"/pushDelay",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse pushOrder2Delay(@RequestBody UserOrderDto userOrderDto){
        BaseResponse response = new BaseResponse(StatusCode.Success);
        UserOrder userOrder=new UserOrder();
        //订单加入数据库
        try {
            BeanUtils.copyProperties(userOrderDto,userOrder);
            userOrder.setStatus(1);
            userOrderMapper.insertSelective(userOrder);
        }catch (Exception e){
            e.printStackTrace();
        }

        //发送订单消息到延时队列
        try{
            Integer id = userOrder.getId();
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("user.order.produce.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("user.order.produce.route.key"));

            rabbitTemplate.convertAndSend(id, new MessagePostProcessor() {
                //在真正的消息发送之前，做最后的处理
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties properties=message.getMessageProperties();
                    properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,Integer.class);
                    return message;
                }
            });
        }catch (Exception e){
            log.error("异常信息:{}",e);
        }
        return response;
    }
}
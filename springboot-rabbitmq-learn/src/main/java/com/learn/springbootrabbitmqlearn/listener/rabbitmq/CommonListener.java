package com.learn.springbootrabbitmqlearn.listener.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.springbootrabbitmqlearn.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * autor:liman
 * createtime:2019/10/21
 * comment:
 */
@Component
public class CommonListener {

    private static final Logger log = LoggerFactory.getLogger(CommonListener.class);

//    @Autowired
//    private Gson gson;
//
    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues="${spring.rabbitmq.learn.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeMessage(@Payload User message){
        try{

//            String result = new String(message,"UTF-8");
//            log.info("接受到的消息:{}",result);
//            User user=objectMapper.readValue(message,User.class);
            log.info("接收到的消息：{}",message);
//            String result = gson.fromJson(new String(message,"UTF-8"),String.class);

//            Map<String,Object> resMap = objectMapper.readValue(message,Map.class);
//            log.info("接受到的消息：{}",resMap);
        }catch (Exception e){
            log.error("监听到消费消息，发生异常:{}",e.fillInStackTrace());
        }
    }

}

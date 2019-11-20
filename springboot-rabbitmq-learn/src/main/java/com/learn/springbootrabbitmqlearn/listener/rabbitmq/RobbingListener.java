package com.learn.springbootrabbitmqlearn.listener.rabbitmq;

import com.learn.springbootrabbitmqlearn.service.ConcurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * autor:liman
 * createtime:2019/10/24
 * comment:
 */
@Component
public class RobbingListener {

    private static final Logger logger = LoggerFactory.getLogger(RobbingListener.class);

//    @Autowired
//    private ObjectMapper objectMapper;

    @Autowired
    private ConcurrencyService concurrencyService;

    @RabbitListener(queues = "${product.robbing.mq.queue.name}",containerFactory = "singleListenerContainer")
    public void consumerService(@Payload String message){
        try{
            String mobile = new String(message);
            logger.info("监听到抢单的手机号:{}",mobile);

            concurrencyService.manageRobbing(String.valueOf(mobile));
        }catch (Exception e){
            logger.error("{}",e.fillInStackTrace());
        }
    }

}

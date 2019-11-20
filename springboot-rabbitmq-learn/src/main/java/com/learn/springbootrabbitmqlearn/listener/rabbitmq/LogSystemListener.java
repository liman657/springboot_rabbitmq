package com.learn.springbootrabbitmqlearn.listener.rabbitmq;

import com.learn.springbootrabbitmqlearn.dto.LogDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * autor:liman
 * createtime:2019/10/28
 * comment:
 */
@Component
public class LogSystemListener {

    private static final Logger log = LoggerFactory.getLogger(LogSystemListener.class);

    @RabbitListener(queues = "${log.system.queue.name}",containerFactory = "multiListenerContainer")
    public void consumerLogInfo(@Payload LogDto logDto){
        try{
            log.info("系统日志监听器监听到的消息:{}",logDto);
        }catch (Exception e){
            log.error("日志监听异常:{}",e.fillInStackTrace());
        }
    }

}

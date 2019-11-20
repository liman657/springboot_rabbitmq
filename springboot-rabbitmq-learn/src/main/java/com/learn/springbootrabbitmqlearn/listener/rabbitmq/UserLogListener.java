package com.learn.springbootrabbitmqlearn.listener.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.springbootrabbitmqlearn.entity.UserLog;
import com.learn.springbootrabbitmqlearn.mapper.UserLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * autor:liman
 * createtime:2019/10/29
 * comment:
 */
@Component
public class UserLogListener {

    private static final Logger log= LoggerFactory.getLogger(UserLogListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserLogMapper userLogMapper;

    @RabbitListener(queues = "${log.user.queue.name}",containerFactory = "singleListenerContainer")
    public void consumerUserLogQueue(@Payload byte[] message){
        try {
            UserLog userLog=objectMapper.readValue(message, UserLog.class);
            log.info("监听消费用户日志 监听到消息： {} ",userLog);

            userLogMapper.insertSelective(userLog);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

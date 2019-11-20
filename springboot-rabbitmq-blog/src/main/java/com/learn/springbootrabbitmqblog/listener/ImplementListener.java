package com.learn.springbootrabbitmqblog.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * autor:liman
 * createtime:2019/10/27
 * comment:
 */
@Component
public class ImplementListener implements ChannelAwareMessageListener {

    private static final Logger log = LoggerFactory.getLogger(ImplementListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try{
            byte[] body = message.getBody();
            String result = objectMapper.readValue(body, String.class);
            log.info("非注解方式开始消费消息:{}",result);
            channel.basicAck(tag,true);
        }catch (Exception e){
            log.error("消费消息异常，异常信息为:{}",e.fillInStackTrace());
        }
    }
}

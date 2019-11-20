package com.learn.springbootrabbitmqlearn.listener.rabbitmq;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.springbootrabbitmqlearn.dto.User;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * autor:liman
 * createtime:2019/10/26
 * comment:
 */
@Component("simpleMessageListener")
public class SimpleMessageListener implements ChannelAwareMessageListener {

    private static final Logger log= LoggerFactory.getLogger(SimpleMessageListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try{
            byte[] body = message.getBody();
            User user = objectMapper.readValue(body,User.class);
            log.info("正常收到的消息为:{},消息tag为:{}",user,tag);
//            throw new RuntimeException("不爽了，就是要丢个错");
            channel.basicAck(tag,true);//这里即为手动确认消费消息机制
        }catch (Exception e){
            log.error("确认消息发生异常:{}",e.fillInStackTrace());
            channel.basicReject(tag,false);
        }
    }
}
package com.learn.springbootrabbitmqlearn.listener.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.springbootrabbitmqlearn.dto.UserOrderDto;
import com.learn.springbootrabbitmqlearn.entity.UserOrder;
import com.learn.springbootrabbitmqlearn.mapper.UserOrderMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * autor:liman
 * createtime:2019/10/27
 * comment:
 */
@Component("userOrderListener")
public class UserOrderListener implements ChannelAwareMessageListener {

    private static final Logger log = LoggerFactory.getLogger(UserOrderListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserOrderMapper userOrderMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try{
            byte[] body = message.getBody();

            UserOrderDto entity = objectMapper.readValue(body,UserOrderDto.class);
            log.info("用户商城下单，消息队列接收到的消息:{}",entity);

            UserOrder userOrder = new UserOrder();
            userOrder.setStatus(3);
            BeanUtils.copyProperties(entity,userOrder);
            userOrderMapper.insertSelective(userOrder);
            channel.basicAck(tag,true);//消息确认
        }catch (Exception e){
            log.error("用户商城下单，发生异常:{}",e);
            channel.basicReject(tag,false);
        }
    }
}

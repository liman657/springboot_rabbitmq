package com.learn.springbootrabbitmqlearn.listener.rabbitmq;

import com.learn.springbootrabbitmqlearn.entity.UserOrder;
import com.learn.springbootrabbitmqlearn.mapper.UserOrderMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * autor:liman
 * createtime:2019/11/2
 * comment:监听订单延时队列中的消息
 */
@Component
public class UserOrderDelayListener {

    private static final Logger log = LoggerFactory.getLogger(UserOrderDelayListener.class);

    @Autowired
    private UserOrderMapper userOrderMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${user.order.deal.queue.name}", durable = "true")
            , exchange = @Exchange(value = "${user.order.deal.exchange.name}", type = ExchangeTypes.TOPIC)
            , key = "${user.order.deal.route.key}")
    )
    public void dealDelayMessage(@Payload Integer message, @Header(AmqpHeaders.DELIVERY_TAG) long delivertTag, Channel channel) {
        log.info("真正开始处理订单消息:{}",message);
        Integer orderId = message;
        UserOrder entity = userOrderMapper.selectByPkAndStatus(orderId, 1);
        if (entity!=null){
            entity.setStatus(3);
            entity.setUpdateTime(new Date());
            userOrderMapper.updateByPrimaryKeySelective(entity);
        }else{
            //TODO：已支付-可能需要异步 减库存-异步发送其他日志消息

        }
    }
}
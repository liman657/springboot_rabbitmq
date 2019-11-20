package com.learn.springbootrabbitmqlearn.listener.rabbitmq;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * autor:liman
 * createtime:2019/11/2
 * comment:消息级别的延时消费者
 */
@Component
public class MessageDelayListener {
    private static final Logger log = LoggerFactory.getLogger(MessageDelayListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${user.message.deal.queue.name}", durable = "true")
            , exchange = @Exchange(value = "${user.message.deal.exchange.name}", type = ExchangeTypes.TOPIC)
            , key = "${user.message.deal.route.key}")
    )
    public void dealDelayMessage(@Payload Integer message, @Header(AmqpHeaders.DELIVERY_TAG) long delivertTag, Channel channel) {
        log.info("真正开始处理延时消息:{},时间为:{}",message,new Date());
    }
}

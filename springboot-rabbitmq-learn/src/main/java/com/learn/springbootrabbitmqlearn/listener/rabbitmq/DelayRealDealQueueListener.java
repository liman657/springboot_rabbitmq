package com.learn.springbootrabbitmqlearn.listener.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


/**
 * autor:liman
 * createtime:2019/10/31
 * comment: 延时队列最终处理消息的队列和交换机
 */
@Component
public class DelayRealDealQueueListener {

    private static final Logger log = LoggerFactory.getLogger(DelayRealDealQueueListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${simple.deal.queue.name}", durable = "true")
                    , exchange = @Exchange(value = "${simple.deal.exchange.name}", type = ExchangeTypes.TOPIC)
                    , key = "${simple.deal.routing.key.name}")
            ,containerFactory = "singleListenerContainer"
    )
    public void dealDelayMessage(@Payload String message, @Header(AmqpHeaders.DELIVERY_TAG) long delivertTag, Channel channel){
        try{
            String result = new String(message);
            log.info("开始处理真正的延时处理消息:{}",result);
//            channel.basicAck(delivertTag,true);
        }catch (Exception e){
            log.error("延时消息异常:{}",e.fillInStackTrace());
        }
    }
}

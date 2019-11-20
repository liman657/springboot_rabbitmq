package com.learn.springbootrabbitmqlearn.config;

import com.learn.springbootrabbitmqlearn.listener.rabbitmq.SimpleMessageListener;
import com.learn.springbootrabbitmqlearn.listener.rabbitmq.UserOrderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steadyjack on 2018/8/20.
 */
@Configuration
public class RabbitmqConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqConfig.class);

    @Autowired
    private Environment env;

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    @Autowired
    private SimpleMessageListener simpleMessageListener;

    /**
     * 单一消费者
     *
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setTxSize(1);
        return factory;
    }

    /**
     * 多个消费者
     *
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.concurrency", int.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.max-concurrency", int.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.prefetch", int.class));
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
            }
        });
        return rabbitTemplate;
    }


    @Bean
    public DirectExchange basicExchange() {
        return new DirectExchange(env.getProperty("spring.rabbitmq.learn.exchange.name"), true, false);
    }

    @Bean
    public Queue basicQueue() {
        return new Queue(env.getProperty("spring.rabbitmq.learn.queue.name"), true);
    }

    @Bean
    public Binding basicBinding() {
        return BindingBuilder.bind(basicQueue()).to(basicExchange()).with(env.getProperty("spring.rabbitmq.learn.routing.key.name"));
    }

    /**
     * 构建用于抢单的交换机和queue
     */
    @Bean
    public DirectExchange robbingExchange() {
        return new DirectExchange(env.getProperty("product.robbing.mq.exchange.name"), true, false);
    }

    @Bean
    public Queue robbingQueue() {
        return new Queue(env.getProperty("product.robbing.mq.queue.name"), true);
    }

    @Bean
    public Binding robbingBinding() {
        return BindingBuilder.bind(robbingQueue()).to(robbingExchange()).with(env.getProperty("product.robbing.mq.routing.key.name"));
    }

    /**
     * TODO：用于构建多个消费者的消息机制
     */
    @Bean("simpleQueue")
    public Queue simpleQueue() {
        return new Queue(env.getProperty("simple.mq.queue.name"));
    }

    @Bean
    public TopicExchange simpleExchange() {
        return new TopicExchange(env.getProperty("simple.mq.exchange.name"));
    }

    @Bean
    public Binding simpleBinding() {
        return BindingBuilder.bind(simpleQueue()).to(simpleExchange()).with(env.getProperty("simple.mq.routing.key.name"));
    }

    @Bean("simpleContainer")
    public SimpleMessageListenerContainer simpleContainer(@Qualifier("simpleQueue") Queue simpleQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(Integer.valueOf(env.getProperty("spring.rabbitmq.listener.concurrency")));
        container.setMaxConcurrentConsumers(Integer.valueOf(env.getProperty("spring.rabbitmq.listener.max-concurrency")));
        container.setPrefetchCount(Integer.valueOf(env.getProperty("spring.rabbitmq.listener.prefetch")));

        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueues(simpleQueue);
        container.setMessageListener(simpleMessageListener);

        return container;
    }

    //TODO:用户商城下单实战

    @Autowired
    private UserOrderListener userOrderListener;

    @Bean(name = "userOrderQueue")
    public Queue userOrderQueue() {
        return new Queue(env.getProperty("user.order.queue.name"), true);
    }

    @Bean(name = "userOrderExchange")
    public TopicExchange userOrderExchange() {
        return new TopicExchange(env.getProperty("user.order.exchange.name"), true, false);
    }

    @Bean(name = "userOrderBinding")
    public Binding userOrderBinding() {
        return BindingBuilder.bind(userOrderQueue()).to(userOrderExchange())
                .with(env.getProperty("user.order.routing.key.name"));
    }

    @Bean("userOrderContainer")
    public SimpleMessageListenerContainer listenerContainerUserOrder(@Qualifier("userOrderQueue") Queue userOrderQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(userOrderQueue);
        container.setMessageListener(userOrderListener);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return container;
    }


    //TODO:日志消息模型
    @Bean(name = "logSystemQueue")
    public Queue logSystemQueue() {
        return new Queue(env.getProperty("log.system.queue.name"), true);
    }

    @Bean(name = "logSystemExchange")
    public TopicExchange logSystemExchange() {
        return new TopicExchange(env.getProperty("log.system.exchange.name"), true, false);
    }

    @Bean(name = "logSystemBinding")
    public Binding logSystemBinding() {
        return BindingBuilder.bind(logSystemQueue())
                .to(logSystemExchange())
                .with(env.getProperty("log.system.routing.key.name"));
    }

    //TODO:用户操作日志消息模型
    @Bean(name = "logUserQueue")
    public Queue logUserQueue() {
        return new Queue(env.getProperty("log.user.queue.name"), true);
    }

    @Bean
    public DirectExchange logUserExchange() {
        return new DirectExchange(env.getProperty("log.user.exchange.name"), true, false);
    }

    @Bean
    public Binding logUserBinding() {
        return BindingBuilder.bind(logUserQueue()).to(logUserExchange()).with(env.getProperty("log.user.routing.key.name"));
    }

    /**
     * 创建延时队列，并将其与二次消息投放的交换机进行绑定
     *
     * @return
     */
    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap();

        args.put("x-dead-letter-exchange", env.getProperty("simple.deal.exchange.name"));
        args.put("x-dead-letter-routing-key", env.getProperty("simple.deal.routing.key.name"));
        args.put("x-message-ttl", 10000);
        return new Queue(env.getProperty("simple.delay.queue.name"), true, false, false, args);
    }

    @Bean
    public TopicExchange produceDelayExchange() {
        return new TopicExchange(env.getProperty("simple.produce.exchange.name"));
    }

    /**
     * 将生产端与延时队列进行绑定
     *
     * @return
     */
    @Bean
    public Binding bindProduceAndDelayQueue() {
        return BindingBuilder.bind(delayQueue()).to(produceDelayExchange())
                .with(env.getProperty("simple.produce.routing.key.name"));
    }

    /**
     * 创建用户订单的延时队列
     */
    @Bean
    public Queue userOrderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", env.getProperty("user.order.deal.exchange.name"));
        args.put("x-dead-letter-routing-key", env.getProperty("user.order.deal.route.key"));
        args.put("x-message-ttl", 10000);
        return new Queue(env.getProperty("user.order.delay.queue.name"), true, false, false, args);
    }

    @Bean
    public TopicExchange userOrderProduceExchange() {
        return new TopicExchange(env.getProperty("user.order.produce.exchange.name"));
    }

    /**
     * 创建队列的绑定
     * @return
     */
    @Bean
    public Binding userOrderProduceQueueBind(){
        return BindingBuilder.bind(userOrderDelayQueue()).to(userOrderProduceExchange())
                .with(env.getProperty("user.order.produce.route.key"));
    }

    //TODO:消息级别的延时配置
    @Bean
    public Queue messageDelayQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", env.getProperty("user.message.deal.exchange.name"));
        args.put("x-dead-letter-routing-key", env.getProperty("user.message.deal.route.key"));
        return new Queue(env.getProperty("user.message.delay.queue.name"), true, false, false, args);
    }

    @Bean
    public TopicExchange messageProduceExchange(){
        return new TopicExchange(env.getProperty("user.message.produce.exchange.name"));
    }

    @Bean
    public Binding messageDelayBinding(){
        return BindingBuilder.bind(messageDelayQueue())
                .to(messageProduceExchange())
                .with(env.getProperty("user.message.produce.route.key"));
    }

}

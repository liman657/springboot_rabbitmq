package com.learn.springbootrabbitmqblog.config;

import com.learn.springbootrabbitmqblog.listener.ImplementListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

/**
 * autor:liman
 * createtime:2019/10/27
 * comment:
 */
@Configuration
public class RabbitConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment env;

    @Bean
    public ConnectionFactory connectionFactory() {
        String host = env.getProperty("spring.rabbitmq.host");
        int port = Integer.valueOf(env.getProperty("spring.rabbitmq.port"));
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(env.getProperty("spring.rabbitmq.username"));
        connectionFactory.setPassword(env.getProperty("spring.rabbitmq.password"));
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
//        template.setRoutingKey(env.getProperty("spring.rabbitmq.learn.routing.key.name"));
//        template.setExchange(env.getProperty("spring.rabbitmq.learn.exchange.name"));
        return template;
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(env.getProperty("spring.rabbitmq.learn.exchange.name"));
    }

    /**
     * 获取队列A
     * @return
     */
    @Bean
    public Queue learnQueue() {
        return new Queue(env.getProperty("spring.rabbitmq.learn.queue.name"), true); //队列持久
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(learnQueue()).to(defaultExchange())
                .with(env.getProperty("spring.rabbitmq.learn.routing.key.name"));
    }

    @Autowired
    private ImplementListener implementListener;

    @Bean
    public SimpleMessageListenerContainer getListenerContainer(@Qualifier("learnQueue") Queue queue){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueues(queue);
        container.setMessageListener(implementListener);
        return container;
    }
}

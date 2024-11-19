package com.example.modulerabbitmqproducer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(rabbitMQProperties.getExchange().getName());
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(rabbitMQProperties.getExchange().getDeadLetter());
    }

    @Bean
    public Queue configureQueuesAndBindings() {
        // Dead Letter 설정 공통 인자
        Queue queue = new Queue(rabbitMQProperties.getQueue().getName(), true);
        queue.addArgument("x-dead-letter-exchange", rabbitMQProperties.getExchange().getDeadLetter());
        queue.addArgument("x-message-ttl", rabbitMQProperties.getTtl());
        return queue;
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(rabbitMQProperties.getQueue().getDeadLetter());
    }

}

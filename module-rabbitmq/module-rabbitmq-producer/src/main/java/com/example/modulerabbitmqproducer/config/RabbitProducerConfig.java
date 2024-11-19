package com.example.modulerabbitmqproducer.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RabbitProducerConfig {
    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.exchange.dead-letter}")
    private String deadLetterExchangeName;

    @Value("${rabbitmq.queues.party.create}")
    private String partyCreateQueue;

    @Value("${rabbitmq.queues.party.cancel}")
    private String partyCancelQueue;

    @Value("${rabbitmq.queues.chat.create}")
    private String chatCreateQueue;

    @Value("${rabbitmq.queues.dead-letter}")
    private String deadLetterQueue;

    @Value("${rabbitmq.ttl}")
    private int messageTtl;

    @Bean
    public DirectExchange mainExchange() {
        DirectExchange exchange = new DirectExchange(exchangeName);
        rabbitAdmin.declareExchange(exchange);
        log.info("Declared Main Exchange: {}", exchangeName);
        return exchange;
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        DirectExchange exchange = new DirectExchange(deadLetterExchangeName);
        rabbitAdmin.declareExchange(exchange);
        log.info("Declared Dead Letter Exchange: {}", deadLetterExchangeName);
        return exchange;
    }

    @Bean
    public Queue partyCreateQueue() {
        return QueueBuilder.durable(partyCreateQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-message-ttl", messageTtl)
                .build();
    }

    @Bean
    public Queue partyCancelQueue() {
        return QueueBuilder.durable(partyCancelQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-message-ttl", messageTtl)
                .build();
    }

    @Bean
    public Queue chatCreateQueue() {
        return QueueBuilder.durable(chatCreateQueue)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-message-ttl", messageTtl)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(deadLetterQueue).build();
    }

//    @Bean
//    public Binding bindPartyCreateQueue() {
//        return BindingBuilder.bind(partyCreateQueue())
//                .to(mainExchange())
//                .with("party.create");
//    }
//
//    @Bean
//    public Binding bindPartyCancelQueue() {
//        return BindingBuilder.bind(partyCancelQueue())
//                .to(mainExchange())
//                .with("party.cancel");
//    }
//
//    @Bean
//    public Binding bindChatCreateQueue() {
//        return BindingBuilder.bind(chatCreateQueue())
//                .to(mainExchange())
//                .with("chat.create");
//    }
//
//    @Bean
//    public Binding bindDeadLetterQueue() {
//        return BindingBuilder.bind(deadLetterQueue())
//                .to(deadLetterExchange())
//                .with("#"); // Dead Letter Queue는 모든 메시지를 받을 수 있도록 설정
//    }

    @PostConstruct
    public void setup() {
        log.info("RabbitMQ Producer Configuration Initialized");
    }

}

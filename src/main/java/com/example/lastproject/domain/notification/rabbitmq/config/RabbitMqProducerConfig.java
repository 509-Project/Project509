package com.example.lastproject.domain.notification.rabbitmq.config;

import com.example.lastproject.domain.notification.rabbitmq.consumer.listener.DynamicNotificationListenerConfigurer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Getter
@Slf4j
public class RabbitMqProducerConfig {

    private final DynamicNotificationListenerConfigurer dynamicRabbitMQListener;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.dead-letter-exchange.name}")
    private String partyDlx;

    private final AmqpAdmin amqpAdmin;

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName);
    }

    // DLX 교환기 설정
    @Bean
    public DirectExchange dlxExchange() {
        DirectExchange dlxExchange = new DirectExchange(partyDlx);
        amqpAdmin.declareExchange(dlxExchange); // DLX 교환기 선언
        return dlxExchange;
    }

    public void createQueueWithDLX(String eventType, String region) {
        String queueName = eventType + "." + region.trim().replace(" ", ".");
        String dlxQueueName = queueName + ".dlq";
        String routingKey = queueName;

        // DLQ 큐 생성
        if (!isQueueExist(dlxQueueName)) {
            Queue dlq = QueueBuilder.durable(dlxQueueName).build();
            amqpAdmin.declareQueue(dlq);
            Binding dlqBinding = BindingBuilder.bind(dlq).to(new DirectExchange(partyDlx)).with(dlxQueueName);
            amqpAdmin.declareBinding(dlqBinding); // DLQ 바인딩
        }

        // 기존 큐 생성 (DLX 설정 포함)
        Queue queue = QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", partyDlx) // DLX 설정
                .withArgument("x-dead-letter-routing-key", dlxQueueName) // DLQ 라우팅 키
                .build();

        // 기본 큐가 없으면 생성
        if (!isQueueExist(queueName)) {
            amqpAdmin.declareQueue(queue);  // 큐 생성

            Binding queueBinding = BindingBuilder.bind(queue)
                    .to(directExchange())
                    .with(routingKey);
            amqpAdmin.declareBinding(queueBinding); // 큐와 교환기 바인딩
            dynamicRabbitMQListener.registerListener(queueName);
            // 큐 생성 후 이벤트 발행
        }

        log.info("Created queue: {}, DLQ: {}", queueName, dlxQueueName);
    }

    // 큐 존재 여부 확인
    private boolean isQueueExist(String queueName) {
        return !Objects.isNull(amqpAdmin.getQueueProperties(queueName));
    }
}


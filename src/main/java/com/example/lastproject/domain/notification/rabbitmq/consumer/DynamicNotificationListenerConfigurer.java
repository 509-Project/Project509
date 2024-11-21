package com.example.lastproject.domain.notification.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicNotificationListenerConfigurer {

    private final ConnectionFactory connectionFactory; // RabbitMQ 연결 정보

    private final Map<String, SimpleMessageListenerContainer> listeners = new ConcurrentHashMap<>(); // 동적 리스너 관리

    /**
     * 동적 리스너 등록
     */
    public void registerListener(String queueName) {
        if (listeners.containsKey(queueName)) {
            log.info("Listener for queue '{}' already exists", queueName);
            return;
        }

        // 리스너 컨테이너 생성
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(message -> handleMessage(queueName, message));
        container.setAcknowledgeMode(AcknowledgeMode.AUTO); // 메시지 자동 확인
        try {
            container.start();
        } catch (Exception e) {
            log.error("Failed to start listener for queue '{}'", queueName, e);
            return;
        }

        listeners.put(queueName, container); // 리스너 저장
        log.info("Listener for queue '{}' has been registered.", queueName);
    }

    /**
     * 동적 리스너 제거
     */
    public void removeListener(String queueName) {
        SimpleMessageListenerContainer container = listeners.remove(queueName);
        if (container != null) {
            container.stop();
            log.info("Listener for queue '{}' has been removed.", queueName);
        } else {
            log.warn("No listener found for queue '{}'", queueName);
        }
    }

    /**
     * 메시지 처리
     */
    private void handleMessage(String queueName, Message message) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Received message from queue '{}': {}", queueName, body);

            // 메시지 로직 (예: 알림 전송)
            processMessage(body);

        } catch (Exception e) {
            log.error("Failed to process message from queue '{}'", queueName, e);
        }
    }

    private void processMessage(String message) {
        // 메시지 처리 로직 (예: 데이터 저장, 외부 알림 등)
        log.info("Processing message: {}", message);
    }

}

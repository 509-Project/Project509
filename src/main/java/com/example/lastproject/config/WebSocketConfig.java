package com.example.lastproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Websocket Endpoint와 Message Broker를 구성하는 클래스
 * @EnableWebSocketMessageBroker : Websocket Server를 사용가능하게하기 위한 애노테이션
 * WebSocketMessageBrokerConfigurer : Websocket 연결을 구현하기위한 메소드를 사용하려고 받는 인터페이스
 */

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private TaskScheduler taskScheduler;

    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * 클라이언트가 Websocket서버에 접속하기 위해 사용할 메서드
     * SockJS() : Websocket을 지원하지 않는 브라우저에 대해서도 Websocket처럼 동작하게 해줌
     * Stomp : 메세지 프로토콜
     * - Websocket은 단지 통신규약이라 통신만 해줄뿐, 특정 유저에게만 보내는 기능을 정의하지 못함. Stomp는 이런 기능을 지원함.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        //cors정책과 충돌하지 않기 위해 patterns로 수정
        //JWT인증을 위해 Interceptor 추가

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS();
    }

    /**
     * 클라이언트에게서 다른 클라이언트에게로 메세지를 전달해주는 메서드
     * 메시지가 전달될 경로 또는 주소가 /app로 시작하는 경우 : 해당 메시지를 특정한 메시지 처리 메서드로 전달.
     * - 특정한 메세지 처리 메서드는 구체적으로 ChatController에서 @MessageMapping이 달려있는 메서드를 의미
     * 메시지가 전달될 경로 또는 주소가 /topic으로 시작하는 경우 : 특정 토픽을 구독하고있는 클라이언트에게 보냄
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(this.taskScheduler);;
    }

}

package com.example.lastproject.domain.chat.controller;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatMessageRequest;
import com.example.lastproject.domain.chat.service.RedisMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    /*
    소켓의 연결과 해제를 감지하기위한 이벤트리스너를 사용하는 클래스
    */

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisMessageListener redisMessageListener;
    private final String CHAT_ROOM_ID = "chatRoomId";

    /**
     * 새로운 유저가 들어왔을 때 호출되는 메서드
     * SessionConnectedEvent가 아닌 SessionConnectEvent사용
     * @return : '사용자이메일 joined!'라는 입장메세지 전송
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {

        // StompHeaderAccessor를 사용하여 세션 정보 접근
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String email = getEmailFromSession(headerAccessor);
        Long chatRoomId = getChatRoomIdFromHeader(headerAccessor);

        if (email != null && chatRoomId != null) {
            // 세션 속성에 chatRoomId 저장(아래 SessionDisconnectEvent에서 사용하기 위함. 저장해놓지 않으면 SessionDisconnectEvent에서 사용할 수 없음)
            if (headerAccessor.getSessionAttributes() != null) {
                headerAccessor.getSessionAttributes().put(CHAT_ROOM_ID, chatRoomId);
                sendChatMessage(chatRoomId, email, ChatMessageRequest.MessageType.JOIN);
                //연결 시, Redis에 Topic으로 추가
                redisMessageListener.enterChattingRoom(chatRoomId);
            }
            else {
                logger.warn("Session attributes are null. Unable to store chatRoomId.");
            }
        }


    }

    /**
     * 유저가 나갔을 때 호출되는 메서드
     * @return : '사용자이메일 left!'라는 퇴장메세지 전송
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        //StompHeaderAccessor를 사용하여 세션 정보 접근
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        //헤더에서 채팅방을 나간 유저의 email과 chatRoomId를 추출
        String email = getEmailFromSession(headerAccessor);

        Long chatRoomId = Optional.ofNullable(headerAccessor.getSessionAttributes())
                .map(attrs -> (Long) attrs.get(CHAT_ROOM_ID))
                .orElse(null);

        if (email != null && chatRoomId != null) {
            sendChatMessage(chatRoomId, email, ChatMessageRequest.MessageType.LEAVE);
        }

    }

    /**
     * 세션에서 이메일을 가져오는 private 메서드
     */
    private String getEmailFromSession(StompHeaderAccessor headerAccessor) {

        return Optional.ofNullable(headerAccessor.getSessionAttributes())
                .map(attrs -> (AuthUser) attrs.get("authUser"))
                .map(AuthUser::getEmail)
                .orElse(null);

    }

    /**
     * 헤더에서 chatRoomId를 가져오는 private 메서드
     */
    private Long getChatRoomIdFromHeader(StompHeaderAccessor headerAccessor) {

        try {
            return Optional.ofNullable(headerAccessor.getFirstNativeHeader(CHAT_ROOM_ID))
                    .map(Long::parseLong)
                    .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }

    }

    /**
     * 채팅 메시지를 생성하고 전송하는 private 메서드
     */
    private void sendChatMessage(Long chatRoomId, String email, ChatMessageRequest.MessageType messageType) {

        ChatMessageRequest chatMessage = new ChatMessageRequest();
        chatMessage.changeType(messageType);
        chatMessage.changeSender(email);

        messagingTemplate.convertAndSend("/topic/" + chatRoomId, chatMessage);

    }

}

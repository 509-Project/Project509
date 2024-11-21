package com.example.lastproject.domain.chat.controller;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.chat.dto.ChatMessageRequest;
import com.example.lastproject.domain.chat.dto.ChatMessageResponse;
import com.example.lastproject.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * 경로를 "/app/chat.sendMessage"로 가지고있는 메세지들이 다뤄지는 메서드
     * @return 입력한 메세지 저장 후 반환
     * return값을 @SendTo의 파라미터에 적힌 토픽으로 보냄
     */
    @MessageMapping("/chat.sendMessage/{chatRoomId}")
    @SendTo("/topic/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId, @Payload ChatMessageRequest chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        // WebSocket 세션에서 AuthUser 객체를 가져옴
        AuthUser authUser = Optional.ofNullable(headerAccessor.getSessionAttributes())
                .map(attrs -> (AuthUser) attrs.get("authUser"))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        chatMessageService.sendMessage(chatRoomId, chatMessage, authUser);
    }

    /**
     * 채팅방에 사용자가 입장했을 때, 입장 전에 존재했던 채팅 메세지들을 보여주는 메서드
     * @param chatRoomId
     * @return
     */
    @GetMapping("/chat/history/{chatRoomId}")
    @ResponseBody
    public List<ChatMessageResponse> getChatHistory(@PathVariable Long chatRoomId) {
        return chatMessageService.getChatHistory(chatRoomId);
    }

}

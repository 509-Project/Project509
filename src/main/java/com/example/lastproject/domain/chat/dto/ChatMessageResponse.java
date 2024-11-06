package com.example.lastproject.domain.chat.dto;

import com.example.lastproject.domain.chat.entity.ChatMessage;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {

    private String sender;
    private String content;

    @Enumerated(EnumType.STRING)
    private ChatMessageRequest.MessageType type;

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.sender = chatMessage.getSender();
        this.content = chatMessage.getContent();
        this.type = chatMessage.getType();
    }

}

package com.example.lastproject.domain.chat.entity;

import com.example.lastproject.domain.chat.dto.ChatMessageRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String content;

    @Enumerated(EnumType.STRING)
    private ChatMessageRequest.MessageType type;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    public ChatMessage(ChatMessageRequest chatMessageRequest, ChatRoom chatRoom) {
        this.sender = chatMessageRequest.getSender();
        this.content = chatMessageRequest.getContent();
        this.type = chatMessageRequest.getType();
        this.chatRoom = chatRoom;
    }

}

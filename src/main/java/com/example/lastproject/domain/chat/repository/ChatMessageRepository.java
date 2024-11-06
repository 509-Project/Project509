package com.example.lastproject.domain.chat.repository;

import com.example.lastproject.domain.chat.entity.ChatMessage;
import com.example.lastproject.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByCreatedAt(ChatRoom chatRoom);

}

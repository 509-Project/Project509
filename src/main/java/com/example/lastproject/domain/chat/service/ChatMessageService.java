package com.example.lastproject.domain.chat.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatMessageRequest;
import com.example.lastproject.domain.chat.dto.ChatMessageResponse;

import java.util.List;

public interface ChatMessageService {

    void sendMessage(Long chatRoomId, ChatMessageRequest chatMessageRequest, AuthUser authUser);

    List<ChatMessageResponse> getChatHistory(Long chatRoomId);

}

package com.example.lastproject.domain.chat.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.chat.dto.ChatMessageRequest;
import com.example.lastproject.domain.chat.dto.ChatMessageResponse;
import com.example.lastproject.domain.chat.entity.ChatMessage;
import com.example.lastproject.domain.chat.entity.ChatRoom;
import com.example.lastproject.domain.chat.repository.ChatMessageRepository;
import com.example.lastproject.domain.chat.repository.ChatRoomRepository;
import com.example.lastproject.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    private AuthUser authUser;
    private ChatRoom chatRoom;
    private ChatMessageRequest chatMessageRequest;

    @BeforeEach
    void setUp() {
        authUser = AuthUser.builder()
                .userId(1L)
                .email("user@example.com")
                .role(UserRole.ROLE_USER)
                .build();

        chatRoom = new ChatRoom();
        ReflectionTestUtils.setField(chatRoom, "id", 1L);

        chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.changeType(ChatMessageRequest.MessageType.CHAT);
        chatMessageRequest.changeSender(authUser.getEmail());
    }

//    @Test
//    void sendMessage_성공() {
//        // given
//        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
//        given(chatMessageRepository.save(any(ChatMessage.class))).willAnswer(invocation -> invocation.getArgument(0));
//
//        // when
//        ChatMessageRequest result = chatMessageService.sendMessage(1L, chatMessageRequest, authUser);
//
//        // then
//        assertThat(result.getSender()).isEqualTo(chatMessageRequest.getSender());
//        assertThat(result.getContent()).isEqualTo(chatMessageRequest.getContent());
//        then(chatMessageRepository).should(times(1)).save(any(ChatMessage.class));
//    }

    @Test
    void sendMessage_실패_채팅방없음() {
        // given
        given(chatRoomRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.sendMessage(1L, chatMessageRequest, authUser)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHATROOM_NOT_FOUND);
    }

    @Test
    void getChatHistory_성공() {
        // given
        ChatMessage chatMessage1 = new ChatMessage(chatMessageRequest, chatRoom);
        ChatMessage chatMessage2 = new ChatMessage(chatMessageRequest, chatRoom);

        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
        given(chatMessageRepository.findByChatRoomOrderByCreatedAt(chatRoom)).willReturn(List.of(chatMessage1, chatMessage2));

        // when
        List<ChatMessageResponse> result = chatMessageService.getChatHistory(1L);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getSender()).isEqualTo(chatMessage1.getSender());
        assertThat(result.get(1).getSender()).isEqualTo(chatMessage2.getSender());
        then(chatMessageRepository).should(times(1)).findByChatRoomOrderByCreatedAt(chatRoom);
    }

    @Test
    void getChatHistory_실패_채팅방없음() {
        // given
        given(chatRoomRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                chatMessageService.getChatHistory(1L)
        );
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHATROOM_NOT_FOUND);
    }

}

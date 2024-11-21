package com.example.lastproject.domain.chat.service;

import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatMessageRequest;
import com.example.lastproject.domain.chat.dto.ChatMessageResponse;
import com.example.lastproject.domain.chat.entity.ChatMessage;
import com.example.lastproject.domain.chat.entity.ChatRoom;
import com.example.lastproject.domain.chat.repository.ChatMessageRepository;
import com.example.lastproject.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;
    private final RedisMessageListener redisMessageListener;

    /**
     * 입력한 채팅메세지를 DB에 저장 후 반환하는 메서드
     *
     * @param chatRoomId         : 채팅방 Id
     * @param chatMessageRequest : 채팅타입, 내용, 보낸사람
     */
    @Transactional
    public void sendMessage(Long chatRoomId, ChatMessageRequest chatMessageRequest, AuthUser authUser) {

        //채팅메세지가 전송되는 채팅방 찾기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND)
        );

        // AuthUser의 email을 ChatMessageRequest의 sender로 설정
        chatMessageRequest.changeSender(authUser.getEmail());

        ChatMessage chatMessage = new ChatMessage(chatMessageRequest, chatRoom);
        chatMessageRepository.save(chatMessage);

        //Redis pub
        redisPublisher.publish(redisMessageListener.getTopic(chatRoomId), chatMessage);

    }

    /**
     * 새로운 사용자가 채팅방에 입장했을 때, 입장 전 기록된 채팅들을 보여주기 위한 메서드
     *
     * @param chatRoomId : 채팅방 Id
     * @return : 입장전 입력되었던 채팅메세지
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Long chatRoomId) {

        //입장한 채팅방 찾기
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND)
        );

        //입장한 채팅방에 존재하는 기존 채팅메세지들을 리스트로 추출
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomOrderByCreatedAt(chatRoom);

        //Dto에 매핑해서 반환
        return chatMessageList.stream()
                .map(ChatMessageResponse::new)
                .toList();

    }

}

package com.example.lastproject.domain.chat.controller;

import com.example.lastproject.common.annotation.LogisticsNotify;
import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatrooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성을 요청하는 메서드
     * @param partyId : 채팅방 생성 대상이되는 Party의 Id값
     * @return : 채팅방 생성
     */
    @PostMapping("/{partyId}")
    @LogisticsNotify
    public ResponseEntity<ChatRoomResponse> createChatRoom(@PathVariable Long partyId, @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(partyId, authUser));
    }

    /**
     * 사용자가 참여한 역대 채팅방들을 조회하는 메서드
     * @param authUser : 사용자 정보가 담긴 객체
     * @return : List형태의 채팅 정보 집합
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(chatRoomService.getChatRooms(authUser));
    }

    /**
     * 채팅방을 삭제하는 메소드
     * @param chatRoomId : 삭제 대상 채팅방의 Id
     * @return : 채팅방 상태값 변경(ACTIVATED -> DELETED)
     */
    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomId, @AuthenticationPrincipal AuthUser authUser) {
        chatRoomService.deleteChatRoom(chatRoomId, authUser);
        return ResponseEntity.noContent().build();
    }

}

package com.example.lastproject.domain.chat.dto;

import com.example.lastproject.domain.chat.entity.ChatRoom;
import com.example.lastproject.domain.chat.enums.ChatRoomStatus;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChatRoomResponse {

    private Long id;
    private String name;
    private Long partyId;
    private ChatRoomStatus status;

    public ChatRoomResponse(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.name = chatRoom.getName();
        this.partyId = chatRoom.getParty().getId();
        this.status = chatRoom.getStatus();
    }

}

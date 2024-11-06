package com.example.lastproject.domain.chat.entity;

import com.example.lastproject.domain.chat.enums.ChatRoomStatus;
import com.example.lastproject.domain.party.entity.Party;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "party_id")
    private Party party;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ChatRoomStatus status;

    public ChatRoom(Party party) {
        this.name = party.getMarketName() + " " + party.getItem().getCategory();
        this.party = party;
        this.status = ChatRoomStatus.ACTIVATED;
    }

    public void deleteChatRoom() {
        this.status = ChatRoomStatus.DELETED;
    }

}

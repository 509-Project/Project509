package com.example.lastproject.domain.chat.repository;

import com.example.lastproject.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    boolean existsByPartyId(Long party_id);

    // partyIds 리스트에 포함된 모든 파티 ID에 해당하는 ChatRoom을 조회
    List<ChatRoom> findAllByParty_IdIn(List<Long> partyIds);

}

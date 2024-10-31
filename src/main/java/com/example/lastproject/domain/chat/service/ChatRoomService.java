package com.example.lastproject.domain.chat.service;

import com.example.lastproject.common.CustomException;
import com.example.lastproject.common.annotation.LogisticsNotify;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.domain.auth.entity.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.chat.entity.ChatRoom;
import com.example.lastproject.domain.chat.repository.ChatRoomRepository;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.repository.PartyRepository;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;

    /**
     * 채팅방을 생성하는 메서드
     * @param partyId : 채팅방 생성 대상이 되는 파티의 Id
     * @return : 새로운 채팅방 정보
     */
    @Transactional
    @LogisticsNotify
    public ChatRoomResponse createChatRoom(Long partyId) {

        //이미 채팅방이 존재하는지 검증
        if(chatRoomRepository.existsByPartyId(partyId)) {
            throw new CustomException(ErrorCode.CHATROOM_RESIST_DUPLICATION);
        }

        //채팅방 생성 대상이 되는 파티가 존재하는지 검증
        Party party = partyRepository.findById(partyId).orElseThrow(
                () -> new CustomException(ErrorCode.PARTY_NOT_FOUND)
        );

        //생성 및 저장
        ChatRoom chatRoom = new ChatRoom(party);
        chatRoomRepository.save(chatRoom);

        return new ChatRoomResponse(chatRoom);

    }

    /**
     * 사용자가 참여한 역대 채팅방들을 조회하는 메서드
     * @param authUser : 사용자 정보가 담긴 객체
     * @return : List형태의 채팅 정보 집합
     */
    public List<ChatRoomResponse> getChatRooms(AuthUser authUser) {

        //PartyMember라는 중간테이블에서 유저가 참가했던 파티들의 List가져오기
        List<PartyMember> parties = partyMemberRepository.findByUserId(authUser.getUserId());

        //응답할 Dto List생성
        List<ChatRoomResponse> chatRoomResponses = new ArrayList<>();

        //유저가 참가했던 각 파티들에 대한 ChatRoom을 찾아 응답Dto에 Mapping
        for(PartyMember partyMember : parties) {
            ChatRoom chatRoom = chatRoomRepository.findByPartyId(partyMember.getParty().getId());
            ChatRoomResponse chatRoomResponse = new ChatRoomResponse(chatRoom);
            chatRoomResponses.add(chatRoomResponse);
        }

        return chatRoomResponses;

    }

    /**
     * 채팅방을 삭제하는 메서드 : 채팅방의 Status값을 ACTIVATED -> DELETED로 변경
     * @param chatRoomId
     */
    @Transactional
    public void deleteChatRoom(Long chatRoomId) {

        //채팅방이 존재하는지 검증
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND)
        );

        chatRoom.deleteChatRoom();

    }

}

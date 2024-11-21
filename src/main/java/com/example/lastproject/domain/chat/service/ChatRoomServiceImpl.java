package com.example.lastproject.domain.chat.service;

import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.common.annotation.LogisticsNotify;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.chat.entity.ChatRoom;
import com.example.lastproject.domain.chat.repository.ChatRoomRepository;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.enums.PartyStatus;
import com.example.lastproject.domain.party.repository.PartyRepository;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
import com.example.lastproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;

    /**
     * 채팅방을 생성하는 메서드
     *
     * @param partyId : 채팅방 생성 대상이 되는 파티의 Id
     * @return : 새로운 채팅방 정보
     */
    @Transactional
    @LogisticsNotify
    public ChatRoomResponse createChatRoom(Long partyId, AuthUser authUser) {

        //이미 채팅방이 존재하는지 검증
        validateChatRoomDoesNotExist(partyId);

        //채팅방 생성 대상이 되는 파티가 존재하는지 검증
        Party party = getPartyById(partyId);
        //대상 파티가 활성화된 상태인지 검증
        validatePartyStatus(party, PartyStatus.JOINED);

        User user = User.fromAuthUser(authUser);
        //대상 파티의 파티장인지 검증
        validateUserIsPartyLeader(party.getId(), user);

        //생성 및 저장
        ChatRoom chatRoom = new ChatRoom(party);
        chatRoomRepository.save(chatRoom);

        return new ChatRoomResponse(chatRoom);

    }

    /**
     * 사용자가 참여한 역대 채팅방들을 조회하는 메서드
     *
     * @param authUser : 사용자 정보가 담긴 객체
     * @return : List형태의 채팅 정보 집합
     */
    public List<ChatRoomResponse> getChatRooms(AuthUser authUser) {

        // 사용자가 참여한 PartyMember 리스트를 가져옴
        List<PartyMember> parties = partyMemberRepository.findByUserId(authUser.getUserId());

        // PartyMember 리스트에서 party ID 수집
        List<Long> partyIds = parties.stream()
                .map(partyMember -> partyMember.getParty().getId())
                .toList();

        // 수집한 party ID 목록을 기반으로 모든 ChatRoom을 한 번의 쿼리로 가져옴
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByParty_IdIn(partyIds);

        // 각 ChatRoom을 ChatRoomResponse로 매핑
        return chatRooms.stream()
                .map(ChatRoomResponse::new)
                .toList();

    }


    /**
     * 채팅방을 삭제하는 메서드 : 채팅방의 Status값을 ACTIVATED -> DELETED로 변경
     *
     * @param chatRoomId
     */
    @Transactional
    public void deleteChatRoom(Long chatRoomId, AuthUser authUser) {

        //채팅방이 존재하는지 검증
        ChatRoom chatRoom = getChatRoomById(chatRoomId);

        Long partyId = chatRoom.getParty().getId();
        //채팅방 삭제 대상이 되는 파티가 존재하는지 검증
        Party party = getPartyById(partyId);

        User user = User.fromAuthUser(authUser);
        //대상 파티의 파티장인지 검증
        validateUserIsPartyLeader(party.getId(), user);

        //대상 채팅방 soft-delete
        chatRoom.deleteChatRoom();

    }

    private void validateChatRoomDoesNotExist(Long partyId) {
        if (chatRoomRepository.existsByPartyId(partyId)) {
            throw new CustomException(ErrorCode.CHATROOM_RESIST_DUPLICATION);
        }
    }

    private Party getPartyById(Long partyId) {
        return partyRepository.findById(partyId).orElseThrow(
                () -> new CustomException(ErrorCode.PARTY_NOT_FOUND)
        );
    }

    private ChatRoom getChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND)
        );
    }

    private void validatePartyStatus(Party party, PartyStatus requiredStatus) {
        if (!party.getPartyStatus().equals(requiredStatus)) {
            throw new CustomException(ErrorCode.INVALID_PARTY_STATUS);
        }
    }

    private void validateUserIsPartyLeader(Long partyId, User user) {
        PartyMember partyMember = partyMemberRepository.findByPartyIdAndUserId(partyId, user.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.PARTY_MEMBER_NOT_FOUND)
        );
        if (!partyMember.getRole().equals(PartyMemberRole.LEADER)) {
            throw new CustomException(ErrorCode.NOT_PARTY_LEADER);
        }
    }

}

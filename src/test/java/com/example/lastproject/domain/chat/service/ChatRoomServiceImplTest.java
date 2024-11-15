//package com.example.lastproject.domain.chat.service;
//
//import com.example.lastproject.common.dto.AuthUser;
//import com.example.lastproject.common.enums.ErrorCode;
//import com.example.lastproject.common.exception.CustomException;
//import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
//import com.example.lastproject.domain.chat.entity.ChatRoom;
//import com.example.lastproject.domain.chat.enums.ChatRoomStatus;
//import com.example.lastproject.domain.chat.repository.ChatRoomRepository;
//import com.example.lastproject.domain.item.entity.Item;
//import com.example.lastproject.domain.party.entity.Party;
//import com.example.lastproject.domain.party.enums.PartyStatus;
//import com.example.lastproject.domain.party.repository.PartyRepository;
//import com.example.lastproject.domain.partymember.entity.PartyMember;
//import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
//import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
//import com.example.lastproject.domain.user.entity.User;
//import com.example.lastproject.domain.user.enums.UserRole;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EnumSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.BDDMockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ChatRoomServiceImplTest {
//
//    @InjectMocks
//    private ChatRoomServiceImpl chatRoomService;
//
//    @Mock
//    private ChatRoomRepository chatRoomRepository;
//
//    @Mock
//    private PartyRepository partyRepository;
//
//    @Mock
//    private PartyMemberRepository partyMemberRepository;
//
//    private AuthUser authUser;
//    private Party party;
//    private Item item;
//    private ChatRoom chatRoom;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        authUser = AuthUser.builder()
//                .userId(1L)
//                .email("user@example.com")
//                .role(UserRole.ROLE_USER)
//                .build();
//
//        user = User.fromAuthUser(authUser);
//        item = new Item("category", "productName");
//
//        Long creatorId = 1L; // 실제 creatorId를 사용해야 합니다.
//        party = new Party("marketName", "marketAddress", item, 1, "itemUnit",
//                LocalDateTime.of(2024, 11, 1, 1, 0), LocalDateTime.of(2024, 11, 2, 1, 0), 4, creatorId);
//
//        ReflectionTestUtils.setField(party, "id", 1L);
//        ReflectionTestUtils.setField(party, "partyStatus", PartyStatus.JOINED);
//
//        chatRoom = new ChatRoom(party);
//        ReflectionTestUtils.setField(chatRoom, "id", 1L);
//    }
//
//    @Test
//    void createChatRoom_성공() {
//        // given
//        given(chatRoomRepository.existsByPartyId(1L)).willReturn(false);
//        given(partyRepository.findById(1L)).willReturn(Optional.of(party));
//        given(partyMemberRepository.findByPartyIdAndUserId(1L, user.getId()))
//                .willReturn(Optional.of(new PartyMember(user, party, PartyMemberRole.LEADER)));
//        given(chatRoomRepository.save(any(ChatRoom.class))).willAnswer(invocation -> invocation.getArgument(0));
//
//        // when
//        ChatRoomResponse result = chatRoomService.createChatRoom(1L, authUser);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getPartyId()).isEqualTo(1L);
//        then(chatRoomRepository).should(times(1)).save(any(ChatRoom.class));
//    }
//
//    @Test
//    void createChatRoom_실패_이미_존재하는_채팅방() {
//        // given
//        given(chatRoomRepository.existsByPartyId(1L)).willReturn(true);
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () ->
//                chatRoomService.createChatRoom(1L, authUser)
//        );
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CHATROOM_RESIST_DUPLICATION);
//    }
//
//    @Test
//    void createChatRoom_실패_파티없음() {
//        // given
//        given(chatRoomRepository.existsByPartyId(1L)).willReturn(false);
//        given(partyRepository.findById(1L)).willReturn(Optional.empty());
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () ->
//                chatRoomService.createChatRoom(1L, authUser)
//        );
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PARTY_NOT_FOUND);
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = PartyStatus.class, names = {"JOINED"}, mode = EnumSource.Mode.EXCLUDE) // JOINED를 제외한 모든 값
//    void createChatRoom_실패_파티상태가_JOINED_아님(PartyStatus partyStatus) {
//        // given
//        given(chatRoomRepository.existsByPartyId(1L)).willReturn(false);
//        given(partyRepository.findById(1L)).willReturn(Optional.of(party));
//        ReflectionTestUtils.setField(party, "partyStatus", partyStatus);
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () ->
//                chatRoomService.createChatRoom(1L, authUser)
//        );
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARTY_STATUS);
//    }
//
//    @Test
//    void createChatRoom_실패_파티장이_아님() {
//        // given
//        given(chatRoomRepository.existsByPartyId(1L)).willReturn(false);
//        given(partyRepository.findById(1L)).willReturn(Optional.of(party));
//        given(partyMemberRepository.findByPartyIdAndUserId(1L, user.getId()))
//                .willReturn(Optional.of(new PartyMember(user, party, PartyMemberRole.MEMBER))); // Member, not leader
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () ->
//                chatRoomService.createChatRoom(1L, authUser)
//        );
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_PARTY_LEADER);
//    }
//
//    @Test
//    void getChatRooms_성공() {
//        // given
//        List<PartyMember> partyMembers = List.of(new PartyMember(user, party, PartyMemberRole.MEMBER));
//        given(partyMemberRepository.findByUserId(authUser.getUserId())).willReturn(partyMembers);
//        given(chatRoomRepository.findAllByParty_IdIn(List.of(1L))).willReturn(List.of(chatRoom));
//
//        // when
//        List<ChatRoomResponse> result = chatRoomService.getChatRooms(authUser);
//
//        // then
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getPartyId()).isEqualTo(1L);
//    }
//
//    @Test
//    void deleteChatRoom_성공() {
//        // given
//        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
//        given(partyRepository.findById(1L)).willReturn(Optional.of(party));
//        given(partyMemberRepository.findByPartyIdAndUserId(1L, user.getId()))
//                .willReturn(Optional.of(new PartyMember(user, party, PartyMemberRole.LEADER)));
//
//        // when
//        chatRoomService.deleteChatRoom(1L, authUser);
//
//        // then
//        assertThat(chatRoom.getStatus()).isEqualTo(ChatRoomStatus.DELETED);
//    }
//
//    @Test
//    void deleteChatRoom_실패_파티없음() {
//        // given
//        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
//        given(partyRepository.findById(1L)).willReturn(Optional.empty());
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () ->
//                chatRoomService.deleteChatRoom(1L, authUser)
//        );
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PARTY_NOT_FOUND);
//    }
//
//
//    @Test
//    void deleteChatRoom_실패_파티장이_아님() {
//        // given
//        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
//        given(partyRepository.findById(1L)).willReturn(Optional.of(party));
//        given(partyMemberRepository.findByPartyIdAndUserId(1L, user.getId()))
//                .willReturn(Optional.of(new PartyMember(user, party, PartyMemberRole.MEMBER))); // Member, not leader
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () ->
//                chatRoomService.deleteChatRoom(1L, authUser)
//        );
//        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_PARTY_LEADER);
//    }
//
//}

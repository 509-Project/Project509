package com.example.lastproject.domain.party.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.item.entity.Item;
import com.example.lastproject.domain.item.repository.ItemRepository;
import com.example.lastproject.domain.party.dto.request.PartyCreateRequest;
import com.example.lastproject.domain.party.dto.request.PartyUpdateRequest;
import com.example.lastproject.domain.party.dto.response.PartyResponse;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.enums.PartyStatus;
import com.example.lastproject.domain.party.repository.PartyRepository;
import com.example.lastproject.domain.partymember.dto.response.PartyMemberResponse;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.enums.PartyMemberInviteStatus;
import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.enums.UserRole;
import com.example.lastproject.domain.user.repository.UserRepository;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PartyServiceTest {

    @InjectMocks
    private PartyService partyService;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private PartyMemberRepository partyMemberRepository;

    @Mock
    private UserRepository userRepository;

    private AuthUser authUser;
    private PartyCreateRequest partyCreateRequest;
    private PartyUpdateRequest partyUpdateRequest;
    private User user;
    private Item item;
    private Party party;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트에 사용할 기본 데이터 설정
        authUser = new AuthUser(1L, "testemail", UserRole.ROLE_USER);
        user = new User(/* ... 초기화 ... */);
        item = new Item(/* ... 초기화 ... */);
        party = new Party(/* ... 초기화 ... */);
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        party = new Party(
                "이마트", "마켓 주소", new BigDecimal("37.5665"),
                new BigDecimal("126.9780"), item, 10, "kg",
                startTime, endTime, 3, user.getId()
        );

        partyCreateRequest = new PartyCreateRequest(
                "이마트", "마켓 주소", new BigDecimal("37.5665"), new BigDecimal("126.9780"),
                1L, 10, "kg", startTime, endTime, 3
        );

        partyUpdateRequest = new PartyUpdateRequest(
                1L, 10, "kg",
                LocalDateTime.now().plusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                3
        );

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
    }

    // 파티 생성 성공
    @Test
    void createParty_success() {
        when(partyRepository.save(any())).thenReturn(party);

        PartyResponse response = partyService.createParty(partyCreateRequest, authUser);

        assertNotNull(response);
        assertEquals("이마트", response.getMarketName());
        verify(partyRepository).save(any());
        verify(partyMemberRepository).save(any());
    }

    // 파티 수정 성공
    @Test
    void updateParty_success() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(partyMemberRepository.findByPartyIdAndUserId(1L, user.getId())).thenReturn(Optional.of(new PartyMember()));

        PartyResponse response = partyService.updateParty(1L, partyUpdateRequest, authUser);

        assertNotNull(response);
        verify(partyRepository).save(party);
    }

    // 파티 수정 시 파티가 존재하지 않을 경우
    @Test
    void updateParty_partyNotFound() {
        when(partyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> partyService.updateParty(1L, partyUpdateRequest, authUser));
    }

    // 파티 완료(장보기 완료) 성공
    @Test
    void completeParty_success() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        partyService.completeParty(1L);

        assertEquals(PartyStatus.DONE, party.getStatus());
        verify(partyRepository).save(party);
    }

    // 완료할 파티가 존재하지 않을 경우
    @Test
    void completeParty_partyNotFound() {
        when(partyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> partyService.completeParty(1L));
    }

    // 파티 완료 후 참여 멤버 조회 성공
    @Test
    void getMembersAfterPartyClosed_success() {

        item = new Item(/* Item 객체 초기화 */);

        // LocalDateTime을 String으로 포맷팅
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 수정된 파티 객체 생성
        party = new Party(
                "이마트",
                "마켓 주소",
                new BigDecimal("37.5665"),
                new BigDecimal("126.9780"),
                item,
                10,
                "kg",
                startTime, // String 타입으로 전달
                endTime,   // String 타입으로 전달
                3,
                user.getId()
        );

        party.completeParty(); // 파티 상태를 DONE으로 설정

        // Mocking repository 호출
        when(partyRepository.findByIdAndCreatorId(1L, user.getId())).thenReturn(Optional.of(party));
        when(partyMemberRepository.findByPartyId(1L)).thenReturn(Collections.singletonList(
                new PartyMember(user, party, PartyMemberRole.MEMBER, PartyMemberInviteStatus.ACCEPTED)));

        // 파티 멤버 목록 가져오기
        List<PartyMemberResponse> members = partyService.getMembersAfterPartyClosed(1L, authUser);

        // 결과 확인
        assertEquals(1, members.size());
    }

    // 유저가 파티에 참여 중인지 확인 성공
    @Test
    void isUserInParty_success() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(partyMemberRepository.findByPartyIdAndUserId(1L, user.getId())).thenReturn(Optional.of(new PartyMember()));

        boolean result = partyService.isUserInParty(1L, authUser);

        assertTrue(result);
    }

    // 파티 취소 성공
    @Test
    void cancelParty_success() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(partyRepository.save(any())).thenReturn(party);

        partyService.cancelParty(1L);

        assertEquals(PartyStatus.CANCELED, party.getStatus());
        verify(partyRepository).save(party);
    }

    // 취소할 파티가 존재하지 않을 경우
    @Test
    void cancelParty_partyNotFound() {
        when(partyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> partyService.cancelParty(1L));
    }

}

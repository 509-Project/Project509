//package com.example.lastproject.domain.penalty.service;
//
//import com.example.lastproject.common.dto.AuthUser;
//import com.example.lastproject.common.exception.CustomException;
//import com.example.lastproject.domain.item.entity.Item;
//import com.example.lastproject.domain.party.entity.Party;
//import com.example.lastproject.domain.party.enums.PartyStatus;
//import com.example.lastproject.domain.party.repository.PartyRepository;
//import com.example.lastproject.domain.partymember.entity.PartyMember;
//import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
//import com.example.lastproject.domain.penalty.dto.request.PenaltyRequest;
//import com.example.lastproject.domain.penalty.entity.Penalty;
//import com.example.lastproject.domain.penalty.repository.PenaltyRepository;
//import com.example.lastproject.domain.user.entity.User;
//import com.example.lastproject.domain.user.enums.UserRole;
//import com.example.lastproject.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class PenaltyServiceTest {
//
//    @Mock
//    private PenaltyRepository penaltyRepository;
//
//    @Mock
//    private PartyRepository partyRepository;
//
//    @Mock
//    private PartyMemberRepository partyMemberRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private PenaltyServiceImpl penaltyService;
//
//    private Party party;
//    private Penalty penalty;
//    private AuthUser authUser;
//    private PartyMember partyMember;
//
//    private PenaltyRequest penaltyRequest;
//
//    @BeforeEach
//    public void setUp() {
//
//        authUser = new AuthUser(
//                1L,
//                "1@1",
//                UserRole.ROLE_ADMIN
//        );
//
//        Item item = new Item();
//
//        LocalDateTime startTime = LocalDateTime.parse("2024-01-01T00:00:00");
//        LocalDateTime endTime = LocalDateTime.parse("2024-01-01T10:00:00");
//
//        Long partyId = 1L;
//        party = new Party(
//                "동동마켓",
//                "사하구",
//                item,
//                3,
//                "2kg",
//                startTime,
//                endTime,
//                3
//        );
//
//        partyMember = new PartyMember(
//
//        );
//
//
//        List<Long> userIds = new ArrayList<>();
//        penaltyRequest = new PenaltyRequest(userIds);
//
////        for (PartyMember user : members) {
////
////            Penalty penalty = new Penalty(party, user.getUser());
////            penalties.add(penalty);
////        }
//
//    }
//
//    /**
//     * 페널티 부여에 성공한 경우
//     */
//    @Nested
//    class SendPenaltySuccess {
//
//        @Test
//        void 페널티_부여_성공하기() {
//
//            // 유저 생성
//            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.ROLE_ADMIN);
//            User.fromAuthUser(authUser);
//
//            // 아이템 생성
//            Item item = new Item();
//
//            // 파티 생성
//            LocalDateTime startTime = LocalDateTime.parse("2024-01-01T00:00:00");
//            LocalDateTime endTime = LocalDateTime.parse("2024-01-01T10:00:00");
//
//            Party party = new Party(
//                    "동동마켓",
//                    "사하구",
//                    item,
//                    3,
//                    "2kg",
//                    startTime,
//                    endTime,
//                    3
//            );
//
//            // 파티 생성
//            partyRepository.save(party);
//            verify(partyRepository, times(1)).save(party); // 파티 생성 성공
//
//            // 파티 신청, 파티 수락 구현
//            // 파티 멤버를 넣어줘야됨 !! 근데 어떻게넣지,,,,,,,,,,,,,,,,,,,
//
//            PartyMember member = new PartyMember(
//
//            );
//
//            // 파티 완료
//            party.completeParty();
//            assertEquals(party.getPartyStatus(), PartyStatus.DONE); // 파티 완료 성공
//
//
//            // 페널티 부여 구현
//
//
//        }
//    }
//
//    /**
//     * 페널티 부여에 실패한 경우
//     */
//    @Nested
//    class SendPenaltyFailure {
//
//        @Test
//        void 파티가_없어서_페널티부여_실패() {
//
//        }
//
//        @Test
//        void 리더가_아니라서_페널티부여_실패() {
//
//        }
//
//        @Test
//        void 파티가_진행중이어서_페널티부여_실패() {
//
//            // given
//            AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.ROLE_ADMIN);
//            PenaltyRequest penaltyRequest = new PenaltyRequest(Arrays.asList(2L, 3L));
//
//            Long partyId = 1L;
//
//            // when & then
//            assertThrows(CustomException.class, () ->
//                    penaltyService.sendPenalty(authUser, partyId, penaltyRequest)
//            );
//
//        }
//    }
//
//}

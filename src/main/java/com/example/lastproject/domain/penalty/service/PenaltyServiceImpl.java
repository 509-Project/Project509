package com.example.lastproject.domain.penalty.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.enums.PartyStatus;
import com.example.lastproject.domain.party.repository.PartyRepository;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.enums.PartyMemberInviteStatus;
import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
import com.example.lastproject.domain.penalty.dto.request.PenaltyRequest;
import com.example.lastproject.domain.penalty.entity.Penalty;
import com.example.lastproject.domain.penalty.repository.PenaltyRepository;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.repository.UserRepository;
import com.example.lastproject.domain.user.service.PenaltyCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final UserRepository userRepository;
    private final PenaltyCountService penaltyCountService;
    private final LettuceLockService lettuceLockService;  // 락을 관리하는 서비스

    /**
     * 페널티 부여
     *
     * @param partyId 페널티를 부여할 유저가 속했던 파티
     * @param request 페널티를 부여할 유저의 리스트
     */
    @Override
    @Transactional
    public void sendPenalty(AuthUser authUser, Long partyId, PenaltyRequest request) {

        // 파티 찾기
        Party party = partyRepository.findByIdAndPartyStatus(partyId, PartyStatus.DONE)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.PARTY_NOT_FOUND)
                );

        // 해당 파티의 해당 유저 아이디와 리더 롤을 가진 사람 찾기
        Optional<PartyMember> partyLeader = partyMemberRepository
                .findByPartyIdAndUserIdAndRole(
                        partyId,
                        authUser.getUserId(),
                        PartyMemberRole.LEADER
                );

        // 리더가 없거나 아니면 요청을 못 보내도록 예외처리
        if (partyLeader.isEmpty()) {
            throw new CustomException(ErrorCode.USER_IS_NOT_LEADER);
        }

        // 페널티를 줄 유저 찾기
        User penaltiedUser = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 해당 파티의 선택된 유저 리스트에서 리더와 거절된 상태가 아닌 멤버 리스트 찾기
        PartyMember member = partyMemberRepository
                .findByPartyIdAndUserIdAndRoleNotAndInviteStatusNot(
                        partyId,
                        penaltiedUser.getId(),
                        PartyMemberRole.LEADER,
                        PartyMemberInviteStatus.REJECTED
                ).orElseThrow(() -> new CustomException(ErrorCode.PARTY_MEMBER_NOT_FOUND));

        // 이미 페널티 내역이 있는지 체크
        if (penaltyRepository.existsByUserIdAndPartyId(penaltiedUser, party)) {
            throw new CustomException(ErrorCode.EXIST_PENALTY);
        }

        // 유저별 락 키
        String lockKey = "penalty_lock:" + request.getUserId() + ":" + partyId;

        boolean lockAcquired = lettuceLockService.acquireLock(lockKey, 5);

        // 락 획득 실패 시 예외 발생
        if (!lockAcquired) {
            throw new CustomException(ErrorCode.DATABASE_LOCK_ERROR);
        }

        log.debug("{}에 락이 획득되었습니다.", LocalDateTime.now());

        // 0.1초 대기 후 재시도
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // InterruptedException 처리 (스레드가 중단되었을 경우)
            Thread.currentThread().interrupt();  // 현재 스레드의 인터럽트 상태를 설정
            throw new CustomException(ErrorCode.LOCK_ACQUISITION_INTERRUPTED);
        }

        Penalty penalty = new Penalty(party, member.getUser());

        // redis 에 유저에 대한 페널티 값을 저장(초기) + 증가
        penaltyCountService.setPenaltyCount(penaltiedUser.getId());
        penaltyCountService.incrementPenaltyCount(penaltiedUser.getId());

        // DB에 페널티 내역 추가
        penaltyRepository.save(penalty);

        // 락 해제하기
        lettuceLockService.releaseLock(lockKey);
        log.debug("{}에 락이 해제되었습니다.", LocalDateTime.now());
    }

}

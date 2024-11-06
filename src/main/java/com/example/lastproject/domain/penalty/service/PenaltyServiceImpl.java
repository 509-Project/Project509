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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;

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

        // 해당 파티의 해당 유저아이디와 리더 롤을 가진 사람 찾기
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

        // 해당 파티의 선택된 유저 리스트에서 리더와 거절된 상태가 아닌 멤버 리스트 찾기
        List<PartyMember> members = partyMemberRepository
                .findByPartyIdAndUserIdInAndRoleNotAndInviteStatusNot(
                        partyId,
                        request.getUserIds(),
                        PartyMemberRole.LEADER,
                        PartyMemberInviteStatus.REJECTED
                );

        // 파티 멤버가 아닌 사람은 예외처리
        for (PartyMember user : members) {
            if (user.getRole() != PartyMemberRole.MEMBER) {
                throw new CustomException(ErrorCode.PARTY_MEMBER_NOT_FOUND);
            }
        }

        // 조건에 해당하는 리스트가 비었다면 예외처리
        if (members.isEmpty()) {
            throw new CustomException(ErrorCode.PARTY_MEMBER_NOT_FOUND);
        }

        List<Penalty> penalties = new ArrayList<>();

        // 지정된 유저를 대상으로 각각 페널티를 부여하고 리스트에 추가
        for (PartyMember user : members) {

            Penalty penalty = new Penalty(party, user.getUser());
            penalties.add(penalty);
        }

        // 추가한 페널티 리스트를 한번에 save
        penaltyRepository.saveAll(penalties);

    }

    ////////////////////////////////////////////////////////////////
    // 페널티 부여 안 할 거면 요청 못 보내게 막기 --> PARTY_MEMBER_NOT_FOUND 처리
    // 파티 초대 거절된 멤버 ( REJECTED )에게는 페널티 부여 x --> REJECTED NOT 처리
    // 탈퇴된 사람들도 페널티를 줄 수 없게 막기
    // 이미 부과한 유저 제외하기
    // 이 파티 아닌 사람 제외하기, 가입만 했으면 전부 페널티 줄 수 있음
}

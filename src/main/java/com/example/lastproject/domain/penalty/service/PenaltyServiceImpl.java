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
import com.example.lastproject.domain.user.service.PenaltyCountService;
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
    private final PenaltyCountService penaltyCountService;

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

        /*
        지정된 유저를 대상으로 각각 페널티를 부여하고 리스트에 추가
        부여된 페널티는 DB에 페널티 내역으로 저장되고, redis 에 user 의 페널티 카운트 키와 그 값이 저장됨
         */
        for (PartyMember user : members) {

            Penalty penalty = new Penalty(party, user.getUser());
            penalties.add(penalty);
            penaltyCountService.setPenaltyCount(user.getId());
            penaltyCountService.incrementPenaltyCount(user.getId());
        }

        // 추가한 페널티 리스트를 한번에 save
        penaltyRepository.saveAll(penalties);
    }

}

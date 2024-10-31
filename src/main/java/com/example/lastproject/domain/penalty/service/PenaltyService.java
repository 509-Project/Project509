package com.example.lastproject.domain.penalty.service;

import com.example.lastproject.common.CustomException;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.domain.auth.entity.AuthUser;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.enums.PartyStatus;
import com.example.lastproject.domain.party.repository.PartyRepository;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
import com.example.lastproject.domain.penalty.dto.request.PenaltyRequest;
import com.example.lastproject.domain.penalty.entity.Penalty;
import com.example.lastproject.domain.penalty.repository.PenaltyRepository;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;

    /**
     * 페널티 부여
     *
     * @param partyId 페널티를 부여할 유저가 속했던 파티
     * @param request 페널티를 부여할 유저의 리스트
     */
    @Transactional
    public void sendPenalty(AuthUser authUser, Long partyId, PenaltyRequest request) {

        Party party = partyRepository.findById(partyId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.PARTY_NOT_FOUND)
                );

        // 파티가 완료되어야 페널티 부여 가능
        if (party.getPartyStatus() != PartyStatus.DONE) {
            throw new CustomException(ErrorCode.INVALID_PARTY_STATUS);
        }

        PartyMember partyLeader = partyMemberRepository.findByPartyIdAndUserId(partyId, authUser.getUserId())
                .orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 리더만 페널티 부여 가능
        if (partyLeader.getRole() != PartyMemberRole.LEADER) {
            throw new CustomException(ErrorCode.USERROLE_FAILED);
        }

        // 유저 리스트에 있는 모든 유저를 레포지토리에서 찾아오기
        List<User> users = userRepository.findAllById(request.getUserIds());
        List<Penalty> penalties = new ArrayList<>();

        // 지정된 유저를 대상으로 각각 페널티를 부여하고 리스트에 추가
        for (User user : users) {

            if (user.getId().equals(partyLeader.getId())) {
                throw new CustomException(ErrorCode.CANNOT_PENALIZE_SELF);
            }

            Penalty penalty = new Penalty(party, user);
            penalties.add(penalty);
        }

        // 추가한 페널티 리스트를 한번에 save
        penaltyRepository.saveAll(penalties);

    }

}

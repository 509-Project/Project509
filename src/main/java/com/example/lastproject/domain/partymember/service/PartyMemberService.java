package com.example.lastproject.domain.partymember.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.repository.PartyRepository;
import com.example.lastproject.domain.party.service.PartyService;
import com.example.lastproject.domain.partymember.dto.request.PartyMemberUpdateRequest;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.enums.PartyMemberInviteStatus;
import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
import com.example.lastproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PartyMemberService {

    private final PartyMemberRepository partyMemberRepository;
    private final PartyRepository partyRepository;
    private final PartyService partyService;

    /**
     * 파티원: 파티에 참가 신청
     *
     * @param partyId  파티의 ID
     * @param authUser 파티에 참가 신청하는 유저 정보
     * @throws CustomException PARTY_NOT_FOUND: "파티를 찾을 수 없습니다."
     * @throws CustomException ALREADY_PARTY_MEMBER: "같은 파티에 중복으로 참가 신청할 수 없습니다."
     */
    public void sendJoinRequest(Long partyId, AuthUser authUser) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        User user = User.fromAuthUser(authUser);

        // 파티 서비스의 isUserInParty 메서드를 활용하여 중복 신청 방지
        if (partyService.isUserInParty(partyId, authUser)) {
            throw new CustomException(ErrorCode.ALREADY_PARTY_MEMBER);
        }

        // 새로운 PartyMember를 생성하여 파티에 신청
        PartyMember partyMember = new PartyMember(user, party, PartyMemberRole.MEMBER, PartyMemberInviteStatus.PENDING);
        partyMemberRepository.save(partyMember);
    }

    /**
     * 파티장 : 내가 생성한 파티에 참가 신청한 유저 목록 조회
     *
     * @param partyId 파티의 ID
     * @return List<PartyMemberUpdateRequest> 승인 대기 중인 신청서 목록
     */
    public List<PartyMemberUpdateRequest> getJoinRequests(Long partyId) {
        // 특정 파티에 대한 모든 파티 멤버 조회
        List<PartyMember> partyMembers = partyMemberRepository.findByPartyId(partyId);

        // 신청서 리스트를 생성
        List<PartyMemberUpdateRequest> requests = new ArrayList<>();
        for (PartyMember member : partyMembers) {
            // 승인 대기 상태의 신청서만 추가
            if (member.getInviteStatus() == PartyMemberInviteStatus.PENDING) {
                requests.add(new PartyMemberUpdateRequest(member.getUser().getId(), member.getInviteStatus()));
            }
        }
        return requests;
    }

}

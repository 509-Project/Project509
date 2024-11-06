package com.example.lastproject.domain.party.service;

import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.common.annotation.LogisticsNotify;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.item.entity.Item;
import com.example.lastproject.domain.item.repository.ItemRepository;
import com.example.lastproject.domain.party.dto.request.PartyCreateRequest;
import com.example.lastproject.domain.party.dto.request.PartyUpdateRequest;
import com.example.lastproject.domain.party.dto.response.PartyResponse;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.enums.PartyStatus;
import com.example.lastproject.domain.party.repository.PartyRepository;
import com.example.lastproject.domain.partymember.dto.request.PartyMemberUpdateRequest;
import com.example.lastproject.domain.partymember.dto.response.PartyMemberResponse;
import com.example.lastproject.domain.partymember.entity.PartyMember;
import com.example.lastproject.domain.partymember.enums.PartyMemberInviteStatus;
import com.example.lastproject.domain.partymember.enums.PartyMemberRole;
import com.example.lastproject.domain.partymember.repository.PartyMemberRepository;
import com.example.lastproject.domain.partymember.service.PartyMemberService;
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
public class PartyService {

    private final PartyRepository partyRepository;
    private final ItemRepository itemRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final UserRepository userRepository;

    /**
     * 파티장 : 파티 생성
     *
     * @param request  파티 생성 시 필요한 정보
     *                 (마켓 이름, 마켓 주소, 거래 품목, 품목 개수, 거래 단위, 장보기 시작&종료 시간, 파티 인원)
     * @param authUser 파티 생성 요청을 한 사용자(파티장)
     * @return PartyResponse 생성된 파티 정보
     * @throws CustomException ITEM_NOT_FOUND: "조회되는 품목이 없습니다."
     * @throws CustomException INVALID_ITEM_COUNT: "개수를 입력해야 합니다."
     * @throws CustomException INVALID_TIME_RANGE: "시작 시간은 종료 시간보다 이전이어야 합니다."
     * @throws CustomException INVALID_MEMBERS_COUNT: "최소 참가 인원은 1명 이상이어야 합니다."
     */
    @Transactional
    @LogisticsNotify
    public PartyResponse createParty(PartyCreateRequest request, AuthUser authUser) {
        User user = User.fromAuthUser(authUser);

        // 거래 품목 조회
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        // 품목 개수 확인
        if (request.getItemCount() < 1) {
            throw new CustomException(ErrorCode.INVALID_ITEM_COUNT);
        }

        // 시간 검증
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new CustomException(ErrorCode.INVALID_TIME_RANGE);
        }

        // 인원 수 검증
        if (request.getMembersCount() < 1) {
            throw new CustomException(ErrorCode.INVALID_MEMBERS_COUNT);
        }

        // 파티 생성
        Party party = new Party(
                request.getMarketName(),
                request.getMarketAddress(),
                item,
                request.getItemCount(),
                request.getItemUnit(),
                request.getStartTime(),
                request.getEndTime(),
                request.getMembersCount(),
                user.getId()

        );

        // 파티 저장
        partyRepository.save(party);

        // 파티 멤버 리더 역할
        PartyMember partyMember = new PartyMember(
                user,
                party,
                PartyMemberRole.LEADER, // 생성 시 자동으로 리더(파티장)로 설정
                PartyMemberInviteStatus.ACCEPTED  // 참가 상태를 ACCEPTED(수락)으로 설정
        );

        // 파티 멤버 저장
        partyMemberRepository.save(partyMember);

        // 생성된 파티 정보 반환
        return new PartyResponse(party);
    }

    /**
     * 파티장: 내가 생성한 파티에 참가 신청한 유저의 상태를 변경합니다.
     *
     * @param partyId    파티 ID
     * @param authUser   현재 로그인한 유저 (파티장 여부 검증을 위해 사용)
     * @param requestDto 상태를 변경할 파티 멤버 ID와 새로운 초대 상태를 포함한 DTO
     * @throws CustomException NOT_PARTY_LEADER: "이 작업은 파티장만 수행할 수 있습니다."
     * @throws CustomException PARTY_MEMBER_NOT_FOUND: "해당 파티 멤버를 찾을 수 없습니다."
     */
    @Transactional
    public void handleJoinRequest(Long partyId, AuthUser authUser, PartyMemberUpdateRequest requestDto) {
        User user = User.fromAuthUser(authUser);
        Party party = partyRepository.findByIdAndCreatorId(partyId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_PARTY_LEADER));

        Long partyMemberId = requestDto.getUserId();
        PartyMemberInviteStatus inviteStatus = requestDto.getInviteStatus();

        // PartyMemberService의 메소드를 직접 호출하지 않고 해당 기능을 PartyService에 옮김
        if (partyMemberId != null && inviteStatus != null) {
            // 초대 상태 업데이트 로직을 직접 구현합니다.
            PartyMember partyMember = partyMemberRepository.findById(partyMemberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PARTY_MEMBER_NOT_FOUND));
            partyMember.updateInviteStatus(inviteStatus);
            partyMemberRepository.save(partyMember);
        }

        // 파티 상태 변경
        verifyPartyStatus(party);
    }

    private void verifyPartyStatus(Party party) {
        int acceptedMemberCount = 0;
        List<PartyMember> partyMembers = party.getPartyMembers();
        for (PartyMember member : partyMembers) {
            if (member.getInviteStatus() == PartyMemberInviteStatus.ACCEPTED) {
                acceptedMemberCount++;
            }
        }

        // 현재 상태를 확인하여, 상태 변경이 필요한지 판단
        if (acceptedMemberCount == party.getMembersCount() && party.getStatus() != PartyStatus.JOINED) {
            party.updateStatus(PartyStatus.JOINED); // 파티 상태를 JOINED(참여)로 업데이트
            partyRepository.save(party);
        }
    }

    /**
     * 파티장 : 장보기 완료, 파티 상태를 DONE으로 변경
     *
     * @param partyId 완료할 파티의 ID
     * @throws CustomException PARTY_NOT_FOUND: "파티를 찾을 수 없습니다."
     */
    @Transactional
    public void completeParty(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        party.completeParty();
    }

    /**
     * 파티장 : 장보기 완료 후 파티에 참여한 멤버 목록 조회
     *
     * @param partyId  파티 ID
     * @param authUser 현재 로그인한 파티장 (파티장 여부 검증)
     * @return List<PartyMemberResponse> 참여 멤버 목록
     * @throws CustomException NOT_PARTY_LEADER: "이 작업은 파티장만 수행할 수 있습니다."
     * @throws CustomException PARTY_NOT_DONE: "파티가 완료되지 않았습니다."
     */
    public List<PartyMemberResponse> getMembersAfterPartyClosed(Long partyId, AuthUser authUser) {
        User user = User.fromAuthUser(authUser);
        Party party = partyRepository.findByIdAndCreatorId(partyId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_PARTY_LEADER));

        if (party.getStatus() != PartyStatus.DONE) {
            throw new CustomException(ErrorCode.PARTY_NOT_DONE);
        }

        List<PartyMemberResponse> members = new ArrayList<>();
        for (PartyMember member : party.getPartyMembers()) {
            if (member.getInviteStatus() == PartyMemberInviteStatus.ACCEPTED) {
                members.add(new PartyMemberResponse(member));
            }
        }
        return members;
    }

    /**
     * 파티장 : 파티 수정
     *
     * @param partyId  수정할 파티의 ID
     * @param request  파티 수정에 필요한 정보 (거래 품목, 품목 개수, 거래 단위, 장보기 시작&종료 시간, 파티 인원)
     * @param authUser 수정 요청을 한 사용자(파티장)
     * @return PartyResponse 수정된 파티 정보
     * @throws CustomException PARTY_NOT_FOUND: "파티를 찾을 수 없습니다."
     * @throws CustomException NOT_PARTY_LEADER: "파티장만 수정할 수 있습니다."
     * @throws CustomException ITEM_NOT_FOUND: "조회되는 품목이 없습니다."
     */
    @Transactional
    public PartyResponse updateParty(Long partyId, PartyUpdateRequest request, AuthUser authUser) {
        User user = User.fromAuthUser(authUser);

        // 파티 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        // 파티장 확인
        PartyMember partyLeader = partyMemberRepository.findByPartyIdAndUserId(partyId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_PARTY_LEADER));

        // 거래 품목 업데이트
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        // 수정할 정보 업데이트
        party.updateDetails(item, request.getItemCount(), request.getItemUnit(), request.getStartTime(), request.getEndTime(), request.getMembersCount());

        return new PartyResponse(party);
    }

    /**
     * 파티장 : 파티 취소, 파티 상태를 CANCELED(취소)로 변경
     *
     * @param partyId 취소할 파티의 ID
     * @throws CustomException PARTY_NOT_FOUND: "파티를 찾을 수 없습니다."
     */
    @Transactional
    @LogisticsNotify
    public void cancelParty(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        party.cancelParty();
    }


    /**
     * 파티원 : 본인이 참가 신청한 모든 파티 목록 조회
     *
     * @param authUser 인증된 사용자
     * @return List<PartyResponse> 사용자가 신청한 모든 파티 목록
     */
    public List<PartyResponse> getPartiesUserApplied(AuthUser authUser) {
        User user = User.fromAuthUser(authUser);
        List<PartyMember> partyMembers = partyMemberRepository.findByUserId(user.getId());

        List<PartyResponse> parties = new ArrayList<>();
        for (PartyMember member : partyMembers) {
            // 모든 파티 상태를 조회
            parties.add(new PartyResponse(member.getParty()));
        }
        return parties;
    }

    /**
     * 파티에 유저가 있는지 확인
     *
     * @param partyId  확인할 파티의 ID
     * @param authUser 인증된 사용자
     * @return boolean 유저가 파티에 존재하면 true, 아니면 false
     * @throws CustomException PARTY_NOT_FOUND: "파티를 찾을 수 없습니다."
     */
    public boolean isUserInParty(Long partyId, AuthUser authUser) {
        User user = User.fromAuthUser(authUser);

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        for (PartyMember member : party.getPartyMembers()) {
            if (member.getUser().getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

}

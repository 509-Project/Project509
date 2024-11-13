package com.example.lastproject.domain.party.service;

import com.example.lastproject.common.annotation.LogisticsNotify;
import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
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
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PartyService {

    private final PartyRepository partyRepository;
    private final ItemRepository itemRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final UserRepository userRepository;


    // 공통으로 사용하는 partyId로 Party 객체를 조회하는 메서드
    private Party findPartyById(Long partyId) {
        return partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));
    }

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

        // 날짜 설정
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String currentYear = String.valueOf(Year.now().getValue());
        LocalDateTime startDateTime = LocalDateTime.parse(currentYear + "-" + request.getStartTime(), dateTimeFormatter);
        LocalDateTime endDateTime = LocalDateTime.parse(currentYear + "-" + request.getEndTime(), dateTimeFormatter);

        // 시작시간이 종료시간 보다 이전인지 확인
        if (startDateTime.isAfter(endDateTime)) {
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
                request.getX(),
                request.getY(),
                item,
                request.getItemCount(),
                request.getItemUnit(),
                startDateTime,
                endDateTime,
                request.getMembersCount(),
                user.getId()
        );

        // 파티 저장
        partyRepository.save(party);

        // 파티 멤버 리더 역할
        PartyMember partyMember = new PartyMember(
                user,
                party,
                PartyMemberRole.LEADER,
                PartyMemberInviteStatus.ACCEPTED
        );

        partyMemberRepository.save(partyMember);
        return new PartyResponse(party, "Leader");
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

        Long userId = requestDto.getUserId();
        PartyMemberInviteStatus inviteStatus = requestDto.getInviteStatus();

        if (userId != null && inviteStatus != null) {
            // 파티 ID와 사용자 ID를 비교하여 파티 멤버 조회
            PartyMember partyMember = partyMemberRepository.findByPartyIdAndUserId(partyId, userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PARTY_MEMBER_NOT_FOUND));
            partyMember.updateInviteStatus(inviteStatus);
        }

        verifyPartyStatus(party);
    }

    /**
     * 파티 상태를 검증하고 업데이트
     *
     * @param party 파티 객체
     */
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
            party.updateStatus(PartyStatus.JOINED);
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
        Party party = findPartyById(partyId);
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
        Party party = findPartyById(partyId);

        partyMemberRepository.findByPartyIdAndUserId(partyId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_PARTY_LEADER));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String currentYear = String.valueOf(Year.now().getValue());
        LocalDateTime startDateTime = LocalDateTime.parse(currentYear + "-" + request.getStartTime(), dateTimeFormatter);
        LocalDateTime endDateTime = LocalDateTime.parse(currentYear + "-" + request.getEndTime(), dateTimeFormatter);

        if (startDateTime.isAfter(endDateTime)) {
            throw new CustomException(ErrorCode.INVALID_TIME_RANGE);
        }

        party.updateDetails(item, request.getItemCount(), request.getItemUnit(), startDateTime, endDateTime, request.getMembersCount());
        return new PartyResponse(party, "Leader");
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
        Party party = findPartyById(partyId);
        party.cancelParty();
    }

    /**
     * 본인이 생성한 파티 및 참가 신청한 파티 목록 조회
     *
     * @param authUser 인증된 사용자
     * @return List<PartyResponse> 내가 생성한 파티와 내가 신청한 파티 목록
     */
    public List<PartyResponse> getMyParties(AuthUser authUser) {
        User user = User.fromAuthUser(authUser);

        // 내가 생성한 파티 조회
        List<Party> createdParties = partyRepository.findAllByCreatorId(user.getId());

        // 내가 참가 신청을 보낸 파티 조회
        List<PartyMember> partyMembers = partyMemberRepository.findByUserId(user.getId());
        List<PartyResponse> partyResponses = new ArrayList<>();

        // 내가 생성한 파티들 추가
        for (Party party : createdParties) {
            partyResponses.add(new PartyResponse(party, "Leader"));
        }

        // 내가 신청한 파티들 추가 (role: "MEMBER", 중복 제거)
        for (PartyMember member : partyMembers) {
            Party party = member.getParty();
            boolean isAlreadyAdded = false;

            // partyResponses 리스트에서 중복된 파티 확인
            for (PartyResponse response : partyResponses) {
                if (response.getId().equals(party.getId())) {
                    isAlreadyAdded = true;
                    break; // 중복이 있으면 더 이상 확인하지 않음
                }
            }

            // 중복되지 않은 경우만 추가
            if (!isAlreadyAdded) {
                partyResponses.add(new PartyResponse(party, "Member"));
            }
        }

        return partyResponses;
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

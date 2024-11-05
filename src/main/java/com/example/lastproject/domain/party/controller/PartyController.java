package com.example.lastproject.domain.party.controller;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.party.dto.request.PartyCreateRequest;
import com.example.lastproject.domain.party.dto.request.PartyUpdateRequest;
import com.example.lastproject.domain.party.dto.response.PartyResponse;
import com.example.lastproject.domain.party.service.PartyService;
import com.example.lastproject.domain.partymember.dto.request.PartyMemberUpdateRequest;
import com.example.lastproject.domain.partymember.dto.response.PartyMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/parties")
public class PartyController {

    private final PartyService partyService;

    /**
     *파티장 : 파티 생성
     *
     * @param authUser 파티 생성 요청을 한 사용자(파티장)
     * @param partyCreateRequest 파티 생성 시 필요한 json body
     *        (marketName: 마켓 이름, marketAddress: 마켓 주소, itemId: 거래 품목, itemCount: 품목 개수, itemUnit: 거래 단위,
     *         startTime: 장보기 시작 시간, endTime: 장보기 종료 시간, membersCount: 파티 인원)
     * @return ResponseEntity<PartyResponse>로 생성된 파티 정보 반환, HTTP 상태 코드 201 반환
     */
    @PostMapping
    public ResponseEntity<PartyResponse> createParty(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody PartyCreateRequest partyCreateRequest) {
        PartyResponse partyResponse = partyService.createParty(partyCreateRequest, authUser);
        return new ResponseEntity<>(partyResponse, HttpStatus.CREATED);
    }

    /**
     * 파티장: 내가 생성한 파티에 참가 신청한 유저 조회 및 상태 변경
     *
     * @param partyId       파티 ID
     * @param authUser      현재 로그인한 파티장 (파티장 여부 검증)
     * @param requestDto    상태를 변경할 파티 멤버 ID와 새로운 초대 상태를 포함한 DTO
     * @return 참가 신청 유저 목록
     */
    @PatchMapping("/{partyId}/join-requests")
    public ResponseEntity<List<PartyMemberUpdateRequest>> handleJoinRequests(
            @PathVariable Long partyId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody PartyMemberUpdateRequest requestDto) {

        List<PartyMemberUpdateRequest> requests = partyService.handleJoinRequests(partyId, authUser, requestDto);
        return ResponseEntity.ok(requests);
    }

    /**
     * 파티장 : 장보기 완료
     *
     * @param partyId 완료할 파티의 ID
     * @return ResponseEntity<Void> 빈 본문과 HTTP 상태 코드 200 반환
     */
    @PutMapping("/{partyId}/complete")
    public ResponseEntity<Void> completeParty(
            @PathVariable Long partyId) {
        partyService.completeParty(partyId);
        return ResponseEntity.ok().build();
    }

    /**
     * 파티장 : 장보기 완료 후 참여한 멤버 목록 조회
     *
     * @param partyId  파티 ID
     * @param authUser 현재 로그인한 파티장 (파티장 여부 검증)
     * @return List<PartyMemberResponse> 참여 멤버 목록
     */
    @GetMapping("/{partyId}/members")
    public ResponseEntity<List<PartyMemberResponse>> getMembersAfterPartyClosed(
            @PathVariable Long partyId,
            @AuthenticationPrincipal AuthUser authUser) {
        List<PartyMemberResponse> members = partyService.getMembersAfterPartyClosed(partyId, authUser);
        return ResponseEntity.ok(members);
    }

    /**
     * 파티장 : 파티 수정
     *
     * @param partyId 수정할 파티의 ID
     * @param partyUpdateRequest 파티 수정에 필요한 json body
     *        (itemId: 거래 품목, itemCount: 품목 개수, itemUnit: 거래 단위,
     *         startTime: 장보기 시작 시간, endTime: 장보기 종료 시간, membersCount: 파티 인원)
     * @param authUser 수정 요청을 한 사용자 ID
     * @return ResponseEntity<PartyResponse> 수정된 파티 정보 반환, HTTP 상태 코드 200 반환
     */
    @PutMapping("/{partyId}")
    public ResponseEntity<PartyResponse> updateParty(
            @PathVariable Long partyId,
            @RequestBody PartyUpdateRequest partyUpdateRequest,
            @AuthenticationPrincipal AuthUser authUser) {
        PartyResponse partyResponse = partyService.updateParty(partyId, partyUpdateRequest, authUser);
        return new ResponseEntity<>(partyResponse, HttpStatus.OK);
    }

    /**
     * 파티장 : 파티 취소
     *
     * @param partyId 취소할 파티의 ID
     * @return ResponseEntity<Void> 빈 본문과 HTTP 상태 코드 200 반환
     */
    @PutMapping("/{partyId}/cancel")
    public ResponseEntity<Void> cancelParty(
            @PathVariable Long partyId) {
        partyService.cancelParty(partyId);
        return ResponseEntity.ok().build();
    }

    /**
     * 파티원 : 본인이 참가 신청한 파티 목록 조회
     *
     * @param authUser 현재 로그인한 사용자
     * @return 사용자가 신청한 파티 목록
     */
    @GetMapping("/my-applications")
    public ResponseEntity<List<PartyResponse>> getPartiesUserApplied(
            @AuthenticationPrincipal AuthUser authUser) {
        List<PartyResponse> responses = partyService.getPartiesUserApplied(authUser);
        return ResponseEntity.ok(responses);
    }

    /**
     * 유저가 파티에 있는지 확인
     *
     * @param partyId 파티 ID
     * @param authUser 인증된 사용자
     * @return ResponseEntity<Boolean> 유저가 파티에 존재하는지 여부
     */
    @GetMapping("/{partyId}/is-member")
    public ResponseEntity<Boolean> isUserInParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal AuthUser authUser) {
        boolean isMember = partyService.isUserInParty(partyId, authUser);
        return ResponseEntity.ok(isMember);
    }

}

package com.example.lastproject.domain.party.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PartyCreateRequest {

    private String marketName; // 마켓 이름
    private String marketAddress; // 마켓 주소
    private Long itemId; // 거래 품목
    private int itemCount; // 품목 개수
    private String itemUnit; // 거래 단위
    private LocalDateTime startTime; // 장보기 시작 시간
    private LocalDateTime endTime; // 장보기 종료 시간
    private int membersCount; // 파티 인원

}

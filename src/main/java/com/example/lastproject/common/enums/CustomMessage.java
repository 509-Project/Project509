package com.example.lastproject.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomMessage {

    // User Success
    SIGNUP_SUCCESS(HttpStatus.OK, "가입이 완료되었습니다."),
    SIGNIN_SUCCESS(HttpStatus.OK, "로그인이 완료되었습니다."),
    WITHDRAWAL_SUCCESS(HttpStatus.OK, "님의 탈퇴가 완료되었습니다."),
    USER_FOUND(HttpStatus.OK, "님이 조회되었습니다."),
    CHANGE_PW_SUCCESS(HttpStatus.OK, "님의 비밀번호 변경이 완료되었습니다."),
    CHANGE_DETAIL_SUCCESS(HttpStatus.OK, "님의 정보 변경이 완료되었습니다."),

    // Penalty Success
    PENALTY_SEND_SUCCESS(HttpStatus.OK, "해당 유저에게 페널티를 부과했습니다."),

    // Item Success
    ON_SUCCESS(HttpStatus.OK, "Success"),

    // Party Success


    // PartyMember Success


    // Market Success


    // LikeItem Success


    // Notification Success

    // Chat Success


    WAS_NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾지 못했습니다.");

    private final HttpStatus status;
    private final String message;

    CustomMessage(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }

}

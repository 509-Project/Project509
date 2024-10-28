package com.example.lastproject.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /**
     * 선언 예시) <br />
     * 오류 코드는 HttpStatus ENUM 클래스 참고  <br />
     * 예외명(대문자+'_') + (HttpStatus.오류코드, "메세지")  <br />
     * STORE_FORBIDDEN(HttpStatus.FORBIDDEN,"사장님 권한을 가진 사용자만 가게를 생성할 수 있습니다."),  <br />
     * STORE_BAD_REQUEST(HttpStatus.BAD_REQUEST,"사장님은 최대 3개의 가게까지만 운영할 수 있습니다."),  <br />
     * STORE_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 가게를 찾을 수 없습니다.");  <br />
     * <br />
     * ======  <br />
     * <br />
     * 서비스 내 사용 예시)  <br />
     * throw new CustomException(ErrorCode.STORE_FORBIDDEN);  <br />
     */


    // Token ErrorCode
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),

    // User ErrorCode
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 조회에 실패했습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "검증에 실패했습니다."),
    SIGNUP_ERROR(HttpStatus.BAD_REQUEST, "가입에 실패했습니다."),
    SIGNIN_ERROR(HttpStatus.BAD_REQUEST, "로그인에 실패했습니다."),
    NO_CONTENTS(HttpStatus.BAD_REQUEST, "변경된 정보가 없습니다."),

    // UserRole ErrorCode
    USERROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "조회에 실패했습니다."),

    // UserPenalty ErrorCode


    // Party ErrorCode
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "파티를 찾을 수 없습니다."),
    INVALID_PARTY_STATUS(HttpStatus.BAD_REQUEST, "잘못된 파티 상태입니다"),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "시작 시간은 종료 시간보다 이전이어야 합니다."),
    INVALID_MAX_MEMBERS(HttpStatus.BAD_REQUEST, "참가 인원 설정이 잘못 되었습니다."),
    INVALID_MIN_MEMBERS(HttpStatus.BAD_REQUEST, "최소 참가 인원은 1명 이상이어야 합니다."),
    NOT_PARTY_LEADER(HttpStatus.BAD_REQUEST, "파티장만 수정할 수 있습니다."),

    // PartyMember ErrorCode
    PARTY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "파티 멤버를 찾을 수 없습니다."),

    // Market ErrorCode


    // Item ErrorCode


    // Notification ErrorCode


    // Chat ErrorCode

    // UserReview ErrorCode


    // UserPenalty ErrorCode


    // Party ErrorCode


    // PartyMember ErrorCode


    // Market ErrorCode
    MARKET_NOT_FOUND(HttpStatus.NOT_FOUND, "마켓 정보를 찾을 수 없습니다."),

    // Item ErrorCode
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 품목을 찾을 수 없습니다."),

    // Notification ErrorCode


    // OpenApi ErrorCode
    API_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "API 요청이 잘못되었습니다."),
    API_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "API 인증키를 찾을 수 없습니다."),
    API_PARSE_ERROR(HttpStatus.BAD_REQUEST, "API 응답데이터 변환에 실패했습니다."),

    // Chat ErrorCode


    // 아래 코드 위에 ErrorCode 작성
    NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾지 못했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }
}

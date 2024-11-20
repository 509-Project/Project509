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
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "존재하는 이메일입니다."),
    EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "존재하는 닉네임입니다."),
    SIGN_IN_ERROR(HttpStatus.BAD_REQUEST, "로그인에 실패했습니다."),
    WITHDRAWAL_ERROR(HttpStatus.BAD_REQUEST, "탈퇴에 실패했습니다."),
    NO_CONTENTS(HttpStatus.BAD_REQUEST, "변경된 정보가 없습니다."),
    ENCODING_FAILED(HttpStatus.BAD_REQUEST, "인코딩에 실패했습니다."),

    // UserRole ErrorCode
    USER_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 권한 조회에 실패했습니다."),
    USER_IS_NOT_LEADER(HttpStatus.NOT_FOUND, "파티장만 접근 가능합니다."),

    // Penalty ErrorCode
    EXIST_PENALTY(HttpStatus.BAD_REQUEST, "이미 페널티를 부여했습니다."),
    DATABASE_LOCK_ERROR(HttpStatus.LOCKED, "락 획득 / 해제에 실패했습니다."),
    LOCK_ACQUISITION_INTERRUPTED(HttpStatus.LOCKED, "스레드가 잠시 중단되었습니다."),
    REDIS_LOCK_ACQUISITION_FAILED(HttpStatus.LOCKED, "Redis 연결에 문제가 발생했습니다."),

    // Party ErrorCode
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "파티를 찾을 수 없습니다."),
    INVALID_PARTY_STATUS(HttpStatus.BAD_REQUEST, "잘못된 파티 상태입니다"),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "시작 시간은 종료 시간보다 이전이어야 합니다."),
    INVALID_MEMBERS_COUNT(HttpStatus.BAD_REQUEST, "최소 참가 인원은 1명 이상이어야 합니다."),
    INVALID_ITEM_COUNT(HttpStatus.BAD_REQUEST, "개수를 입력해야 합니다."),
    NOT_PARTY_LEADER(HttpStatus.BAD_REQUEST, "이 작업은 파티장만 수행할 수 있습니다."),
    PARTY_NOT_DONE(HttpStatus.BAD_REQUEST, "장보기 완료가 되지 않았습니다."),

    // PartyMember ErrorCode
    PARTY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "파티 멤버를 찾을 수 없습니다."),
    ALREADY_PARTY_MEMBER(HttpStatus.BAD_REQUEST, "같은 파티에 중복으로 참가 신청할 수 없습니다."),

    // Market ErrorCode
    MARKET_NOT_FOUND(HttpStatus.NOT_FOUND, "마켓 정보를 찾을 수 없습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "조회되는 주소가 없습니다."),

    // Item ErrorCode
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "조회되는 품목이 없습니다."),
    DUPLICATE_ITEM(HttpStatus.BAD_REQUEST, "이미 등록된 물품입니다."),

    // Notification ErrorCode
    SSE_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "SSE 연결 오류입니다."),
    NOT_FOUND_NOTIFICATION(HttpStatus.BAD_REQUEST, "존재하지 않는 알림입니다."),

    // Chat ErrorCode
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CHATROOM_RESIST_DUPLICATION(HttpStatus.BAD_REQUEST, "이미 존재하는 채팅방입니다"),

    // OpenApi ErrorCode
    API_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "API 요청이 잘못되었습니다."),
    API_CONFIGURATION_NOT_FOUND(HttpStatus.NOT_FOUND, "API 환경설정 값을 찾을 수 없습니다."),
    API_PARSE_ERROR(HttpStatus.BAD_REQUEST, "API 응답데이터 변환에 실패했습니다."),
    JOB_EXECUTION_ERROR(HttpStatus.BAD_REQUEST, "배치 작업 실행에 실패했습니다."),

    // Chat ErrorCode


    // User Access ErrorCode
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "해당 작업에 대한 권한이 없습니다."),


    // 아래 코드 위에 ErrorCode 작성
    NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾지 못했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }

}

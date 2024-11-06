package com.example.lastproject.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비밀번호 오류 Exception
     * @param ex 오류 상태, 메시지
     * @return 해당 내용이 담긴 에러 객체
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handlePasswordMismatchException(CustomException ex) {
        return getErrorResponse(ex.getErrorCode().getStatus(), ex.getMessage());
    }

    /**
     * 에러 객체를 반환하는 메서드
     * @param status 오류 상태
     * @param message 오류 메시지
     * @return 해당 내용이 담긴 에러 객체
     */
    public ResponseEntity<Map<String, Object>> getErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.name());
        errorResponse.put("code", status.value());
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * 포괄 예외 처리 <br />
     * -
     * 이 핸들러는 세부 예외 처리보다 낮은 우선 순위로 처리되는 예외 처리 핸들러입니다. <br />
     * 주로 예기치 않은 에러 상황에 해당 에러가 반환됩니다.
     * @param ex 오류 상태
     * @return 오류 응답을 포함한 ResponseEntity 객체
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}

package com.example.lastproject.domain.notification.controller;

import com.example.lastproject.domain.auth.entity.AuthUser;
import com.example.lastproject.domain.notification.dto.response.NotificationListResponse;
import com.example.lastproject.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * SSE 연결
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @param lastEventId 클라이언트가 마지막으로 수신한 데이터의 Id값을 의미한다. 이를 이용하여 유실된 데이터를 다시 보내줄 수 있다.
     * @return 클라이언트와 연결을 담당하는 SseEmitter 객체 반환
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal AuthUser authUser,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.subscribe(authUser, lastEventId));
    }

    /**
     * 알림 목록 조회
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @return 알림 리스트 반환
     */
    @GetMapping
    public ResponseEntity<NotificationListResponse> getNotifications(@AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(notificationService.getNotifications(authUser));
    }

    /**
     * 알림 읽음으로 변경
     * @param notificationId 읽음 상태로 변경할 알림의 고유 ID
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @return 성공 메시지
     */
    @PatchMapping(value = "/{notificationId}")
    public ResponseEntity<String> readNotification(@PathVariable Long notificationId,
                                                        @AuthenticationPrincipal AuthUser authUser) {
        notificationService.readNotification(notificationId, authUser);
        return ResponseEntity.ok("알림이 읽음으로 변경되었습니다.");
    }

    /**
     * 알림 삭제
     * @param notificationId 삭제할 알림의 고유 ID
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @return 성공 메시지
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId,
                                                                   @AuthenticationPrincipal AuthUser authUser) {
        notificationService.deleteNotification(notificationId, authUser);
        return ResponseEntity.ok("알림이 성공적으로 삭제되었습니다.");
    }

}

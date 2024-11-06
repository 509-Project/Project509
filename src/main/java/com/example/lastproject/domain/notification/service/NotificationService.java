package com.example.lastproject.domain.notification.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.notification.dto.response.NotificationListResponse;
import com.example.lastproject.domain.notification.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    // 찜한 품목의 파티가 생성된 경우 알림
    void notifyUsersAboutPartyCreation(AuthUser authUser, String itemName, Long partyId);

    // 찜한 품목의 파티가 취소된 경우 알림
    void notifyUsersAboutPartyCancellation(AuthUser authUser);

    // 참가 신청한 파티의 채팅창이 생성된 경우 알림
    void notifyUsersAboutPartyChatCreation(AuthUser authUser, ChatRoomResponse chatRoomResponse);

    // SSE 연결
    SseEmitter subscribe(AuthUser authUser, String lastEventId);

    // 알림을 저장하고, 저장된 알림을 클라이언트에게 전송합니다.
    void send(AuthUser authUser, Notification notification);

    // 비동기적으로 알림을 전송합니다.
    void sendNotification(AuthUser authUser, Notification notification);

    // 알림 저장
    Notification saveNotification(AuthUser authUser, Notification notification);

    // 사용자의 알림 목록을 조회합니다.
    NotificationListResponse getNotifications(AuthUser authUser);

    // 알림을 읽음 처리합니다.
    void readNotification(Long notificationId, AuthUser authUser);

    // 알림을 삭제합니다.
    void deleteNotification(Long notificationId, AuthUser authUser);

    // 특정 ID의 알림을 조회하고 권한을 검증합니다.
    void verifyNotificationAccess(Long notificationId, AuthUser authUser);

}

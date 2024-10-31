package com.example.lastproject.domain.notification.service;

import com.example.lastproject.domain.auth.entity.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.market.entity.Market;
import com.example.lastproject.domain.notification.dto.request.NotificationRequest;
import com.example.lastproject.domain.notification.dto.response.NotificationListResponse;
import com.example.lastproject.domain.notification.entity.Notification;
import com.example.lastproject.domain.party.dto.response.PartyResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    // 찜한 품목의 파티가 생성된 경우 알림
    void notifyUsersAboutPartyCreation(AuthUser authUser, PartyResponse partyResponse);

    // 찜한 품목의 파티가 취소된 경우 알림
    void notifyUsersAboutPartyCancellation(AuthUser authUser, Market market);

    // 참가 신청한 파티의 채팅창이 생성된 경우 알림
    void notifyUsersAboutPartyChatCreation(AuthUser authUser, ChatRoomResponse chatRoomResponse);

    SseEmitter subscribe(AuthUser authUser, String lastEventId);

    void send(AuthUser authUser, NotificationRequest request);

    void sendToClient(SseEmitter emitter, String emitterId, String eventId, Object data);

    NotificationListResponse getNotifications(AuthUser authUser);

    void readNotification(Long notificationId, AuthUser authUser);

    void deleteNotification(Long notificationId, AuthUser authUser);

    Notification findNotification(Long notificationId);

}

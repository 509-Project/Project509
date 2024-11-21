package com.example.lastproject.domain.notification.dto;

import com.example.lastproject.domain.notification.entity.Notification;
import com.example.lastproject.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private final Long id;
    private final String content;
    private final NotificationType type;
    private final String url;
    private final Boolean isRead;
    private final LocalDateTime createdAt;

    public static NotificationResponse of(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .type(notification.getNotificationType())
                .url(notification.getUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}

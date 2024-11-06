package com.example.lastproject.domain.notification.dto.response;

import com.example.lastproject.domain.notification.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationListResponse {

    private final List<NotificationResponse> notifications;

    public static NotificationListResponse of(List<Notification> notifications) {
        return NotificationListResponse.builder()
                .notifications(notifications.stream().map(NotificationResponse::of)
                        .toList())
                .build();
    }

}

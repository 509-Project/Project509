package com.example.lastproject.domain.notification.entity;

import com.example.lastproject.common.Timestamped;
import com.example.lastproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private boolean isRead;

    @Builder.Default
    @Column(nullable = false)
    private String message = "Default message";



    public void read() {
        this.isRead = true;
    }

}

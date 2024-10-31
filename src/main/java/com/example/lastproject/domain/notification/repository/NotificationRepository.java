package com.example.lastproject.domain.notification.repository;

import com.example.lastproject.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(Long id);

}

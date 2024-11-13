package com.example.lastproject.domain.penalty.repository;

import com.example.lastproject.domain.penalty.entity.Penalty;
import com.example.lastproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    // 특정 유저의 페널티 횟수를, 최근 기간 이내로 카운트
    long countByUserIdAndCreatedAtGreaterThanEqual(User user, LocalDateTime cutoffDate);

}

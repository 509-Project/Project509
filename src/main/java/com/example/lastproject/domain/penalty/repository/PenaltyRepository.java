package com.example.lastproject.domain.penalty.repository;

import com.example.lastproject.domain.penalty.entity.Penalty;
import com.example.lastproject.domain.penalty.enums.PenaltyStatus;
import com.example.lastproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    List<Penalty> findPenaltiesByUserIdAndStatus(User user, PenaltyStatus status);

    Long countPenaltiesById(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Penalty p SET p.status = :status WHERE p.createdAt < :date")
    void updatePenaltyStatusBeforeDate(@Param("date") LocalDateTime date, @Param("status") PenaltyStatus status);

}

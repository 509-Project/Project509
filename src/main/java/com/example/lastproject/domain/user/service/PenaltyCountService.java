package com.example.lastproject.domain.user.service;

import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.penalty.repository.PenaltyRepository;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PenaltyCountService {

    /*
    각각의 유저에 대한 페널티 횟수를 조회하는 빈도가 높음
    -> Redis 캐시 메모리에 유저의 페널티 집계를 저장하고
    시스템의 성능을 높임과 동시에 사용자 경험을 개선하도록 함
     */

    private final RedisTemplate<String, String> redisTemplate;
    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 페널티 집계 조회
     *
     * @param userId 조회할 사용자
     * @return 집계된 페널티
     */
    public String getPenaltyCount(long userId) {

        String key = "user:" + userId + ":penaltyCount";
        String penaltyCount = redisTemplate.opsForValue().get(key);

        if (penaltyCount == null) setPenaltyCount(userId);
        return penaltyCount;
    }

    /**
     * 페널티 집계를 초기화하고, Redis 에 새로 저장
     *
     * @param userId 집계를 초기화할 사용자
     */
    public void setPenaltyCount(long userId) {
        String key = "user:" + userId + ":penaltyCount";
        LocalDateTime get90DaysAgoDate = LocalDateTime.now().minusDays(90);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // DB 에서 해당 유저의 페널티 집계 값을 가져오고, Redis 에 새로 저장 (TTL - 24시간)
        String penaltyCount = String.valueOf(
                penaltyRepository.countByUserIdAndCreatedAtGreaterThanEqual(
                        user, get90DaysAgoDate)
        );
        redisTemplate.opsForValue().set(key, penaltyCount, 24, TimeUnit.HOURS);
    }

    /**
     * 사용자가 페널티를 받을 때마다 페널티 집계값을 1씩 증가
     *
     * @param userId 집계를 증가시킬 사용자
     */
    public void incrementPenaltyCount(long userId) {
        String key = "user:" + userId + ":penaltyCount";
        redisTemplate.opsForValue().increment(key, 1);
    }
}

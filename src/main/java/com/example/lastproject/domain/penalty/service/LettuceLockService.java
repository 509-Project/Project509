package com.example.lastproject.domain.penalty.service;

import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LettuceLockService {

    /*
    Redis 분산 락을 이용하여 동시다발적인 요청에 대해
    데이터 정합성을 지키기 위해서 락을 획득 / 해제함
     */

    private final StringRedisTemplate redisTemplate;

    /**
     * 락을 획득
     *
     * @param lockKey 락 키
     * @param timeout 초 단위로 락 타임아웃 설정
     * @return 락 획득 성공 여부
     */
    public boolean acquireLock(String lockKey, long timeout) {

        // setIfAbsent -> 락 획득
        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey,
                        "locked",
                        timeout,
                        java.util.concurrent.TimeUnit.SECONDS);

        return lockAcquired != null && lockAcquired;
    }

    /**
     * 락을 해제하는 메서드
     *
     * @param lockKey 락 키
     */
    public void releaseLock(String lockKey) {

        // 락을 해제 (키 삭제)
        Boolean isDeleted = redisTemplate.delete(lockKey);
        if (isDeleted == null || !isDeleted) {
            throw new CustomException(ErrorCode.DATABASE_LOCK_ERROR);
        }
    }
}

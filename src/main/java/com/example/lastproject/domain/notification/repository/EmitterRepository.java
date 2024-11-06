package com.example.lastproject.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String emitterId, Object event);

    // findAllEmitterStartWithByUserId 는 해당 user와 관련된 모든 emitter를 찾습니다.
    Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId);

    // findAllEventCacheStartWithByUserId 는 해당 user와 관련된 모든 event를 찾습니다.
    Map<String, Object> findAllEventCacheStartWithByUserId(String userId);

    void deleteById(String emitterId);

    void deleteAllEmitterStartWithUserId(String userId);

    void deleteAllEventCacheStartWithId(String userId);

}

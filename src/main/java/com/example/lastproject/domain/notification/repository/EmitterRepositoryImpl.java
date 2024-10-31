package com.example.lastproject.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    // Emitter 저장
    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    // 이벤트를 저장한다.
    @Override
    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId, event);
    }

    // 해당 회원과 관련된 모든 Emitter를 찾는다.
    // 브라우저당 여러개의 연결이 가능하기에 여러 Emitter가 존재할 수 있다.
    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 해당 회원과 관련된 모든 이벤트를 찾는다.
    @Override
    public Map<String, Object> findAllEventCacheStartWithByUserId(String userId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Emitter를 지운다.
    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

    // 해당 회원과 관련된 모든 Emitter를 지운다.
    @Override
    public void deleteAllEmitterStartWithUserId(String userId) {
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(userId)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    // 해당 회원과 관련된 모든 이벤트를 지운다.
    @Override
    public void deleteAllEventCacheStartWithId(String userId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(userId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }

}

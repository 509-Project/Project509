package com.example.lastproject.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class RedisMessageListener {

    private static final Map<Long, ChannelTopic> TOPICS = new HashMap<>();

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;

    /**
     * 채팅 연결시, 해당 채팅방을 TOPIC으로 등록하는 메서드
     * @param chattingRoomId : 채팅방 Id값
     */
    public void enterChattingRoom(Long chattingRoomId) {
        ChannelTopic topic = getTopic(chattingRoomId);
        if (topic == null) {
            topic = new ChannelTopic(String.valueOf(chattingRoomId));
            redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
            TOPICS.put(chattingRoomId, topic);
        }
    }

    public ChannelTopic getTopic(Long chattingRoomId) {
        return TOPICS.get(chattingRoomId);
    }
}
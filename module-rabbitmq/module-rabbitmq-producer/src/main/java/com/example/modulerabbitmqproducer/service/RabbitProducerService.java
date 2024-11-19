package com.example.modulerabbitmqproducer.service;

public interface RabbitProducerService {
    void sendPartyCreateMessage(Long partyId, Long userId, Long marketId, Long itemId);
    void sendPartyCancelMessage(Long partyId);
    void sendChatRoomCreateMessage(Long partyId);
}

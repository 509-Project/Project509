//package com.example.modulerabbitmqproducer.service;
//
//import com.example.lastproject.common.enums.ErrorCode;
//import com.example.lastproject.common.exception.CustomException;
//import com.example.lastproject.domain.item.entity.Item;
//import com.example.lastproject.domain.item.repository.ItemRepository;
//import com.example.lastproject.domain.market.entity.Market;
//import com.example.lastproject.domain.market.repository.MarketRepository;
//import com.example.lastproject.domain.party.entity.Party;
//import com.example.lastproject.domain.party.repository.PartyQueryRepositoryImpl;
//import com.example.lastproject.domain.party.repository.PartyRepository;
//import com.example.lastproject.domain.user.dto.NearbyBookmarkUserDto;
//import com.example.lastproject.domain.user.entity.User;
//import com.example.lastproject.domain.user.repository.UserRepository;
//import com.example.modulerabbitmqproducer.config.RabbitMQProperties;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class RabbitProducerServiceImpl implements RabbitProducerService {
//
//    private final RabbitMQProperties rabbitMQProperties; // RabbitMQ 설정 주입
//    private final RabbitTemplate rabbitTemplate; // RabbitMQ 템플릿
//    private final MarketRepository marketRepository;
//    private final ItemRepository itemRepository;
//    private final PartyQueryRepositoryImpl partyQueryRepository;
//    private final PartyRepository partyRepository;
//    private final UserRepository userRepository;
//
//    @Override
//    public void sendPartyCreateMessage(Long partyId, Long userId, Long marketId, Long itemId) {
//        // 이벤트와 이벤트 타입을 확인하여 라우팅 키를 동적으로 생성
//        String event = "party"; // 고정 값 (파티 관련 이벤트)
//        String eventType = "create"; // 고정 값 (취소 관련 이벤트)
//
//        // 유저 아이디 가져오기
//        User user = validateUserExists(userId);
//
//        Party party = validatePartyExists(partyId);
//
//        // 마켓 정보 가져오기
//        Market market = validateMarketExists(marketId);
//
//        // 품목 정보 가져오기
//        Item item = validateItemExists(itemId);
//
//        // 파티 생성 시 주변 10Km 이내의 사용자 찾기
//        List<NearbyBookmarkUserDto> nearbyUsers = partyQueryRepository.getUserIdWithDistanceNearbyParty(user.getLatitude(), user.getLongitude(), itemId);
//
//        if (nearbyUsers.isEmpty()) {
//            throw new CustomException(ErrorCode.NO_NEARBY_USERS);
//        }
//
//        String routingKey = String.format("%s.%s.%s.%s.%s", market.getMarketAddress(), party.getMarketName(), party.getItem().getProductName(), event, eventType);
//
//        // RabbitMQ로 메시지 발송
//        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange().getName(), routingKey);
//
//        // 추가 로직 (예: 알림 발송, DB 업데이트 등)
//        // 알림 서비스 호출 (필요시)
////        notifyUsers(nearbyUsers, market, item);
//    }
//
//    @Override
//    public void sendPartyCancelMessage(Long partyId) {
//        // 이벤트와 이벤트 타입을 확인하여 라우팅 키를 동적으로 생성
//        String event = "party"; // 고정 값 (파티 관련 이벤트)
//        String eventType = "cancel"; // 고정 값 (취소 관련 이벤트)
//
//        // 파티 취소
//        Party party = validatePartyExists(partyId);
//
//        // 라우팅 키 생성
//        String routingKey = String.format("%s.%s.%s", event, eventType, party.getMarketAddress()); // "party.cancel.gangnam"
//
//        // RabbitMQ로 메시지 발송
//        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange().getName(), routingKey);
//
//        // 로그 출력
//        log.info("파티창 취소 메시지 전송");
//    }
//
//
//    @Override
//    public void sendChatRoomCreateMessage(Long partyId) {
//        // 이벤트와 이벤트 타입을 확인하여 라우팅 키를 동적으로 생성
//        String event = "chat"; // 고정 값 (채팅 관련 이벤트)
//        String eventType = "cancel"; // 고정 값 (생성 관련 이벤트)
//
//        Party party = validatePartyExists(partyId);
//
//        // 라우팅 키 생성
//        String routingKey = String.format("%s.%s.%s", event, eventType, party.getMarketAddress()); // "chat.cancel.gangnam"
//
//        // RabbitMQ로 메시지 발송
//        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange().getName(), routingKey);
//
//        // 로그 출력
//        log.info("채팅창 취소 메시지 전송");
//    }
//
//    private Item validateItemExists(Long itemId) {
//        return itemRepository.findById(itemId)
//                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
//    }
//
//    private Market validateMarketExists(Long marketId) {
//        return marketRepository.findById(marketId)
//                .orElseThrow(() -> new CustomException(ErrorCode.MARKET_NOT_FOUND));
//    }
//
//    private Party validatePartyExists(Long partyId) {
//        return partyRepository.findById(partyId)
//                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));
//    }
//
//    private User validateUserExists(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//    }
//
//    // 로그
//    private void notifyUsers(List<NearbyBookmarkUserDto> users, Market market, Item item) {
//        for (NearbyBookmarkUserDto user : users) {
//            log.info("파티 생성 알림 : UserId: {}, MarketName: {}, ProductName: {}",
//                    user.getUserId(), market.getMarketName(), item.getProductName());
//        }
//    }
//
//}

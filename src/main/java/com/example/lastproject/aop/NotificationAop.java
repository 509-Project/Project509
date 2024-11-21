package com.example.lastproject.aop;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.likeitem.dto.response.LikeItemResponse;
import com.example.lastproject.domain.likeitem.repository.LikeItemQueryRepository;
import com.example.lastproject.domain.notification.rabbitmq.config.RabbitMqProducerConfig;
import com.example.lastproject.domain.notification.service.NotificationService;
import com.example.lastproject.domain.party.dto.response.PartyResponse;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.repository.PartyQueryRepositoryImpl;
import com.example.lastproject.domain.party.repository.PartyRepository;
import com.example.lastproject.domain.user.dto.NearbyBookmarkUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationAop {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProducerConfig rabbitMqConfig;

    private final PartyQueryRepositoryImpl partyQueryRepository;
    private final LikeItemQueryRepository likeItemQueryRepository;  // 찜한 품목 조회를 위한 repository 추가
    private final PartyRepository partyRepository;
    private final NotificationService notificationService;

    @Pointcut("execution(* com.example.lastproject.domain.party.service.PartyService.createParty(..))")
    private void partyCreate() {
    }

    @Pointcut("execution(* com.example.lastproject.domain.party.service.PartyService.cancelParty(..))")
    private void partyCancel() {
    }

    @Pointcut("execution(* com.example.lastproject.domain.chat.service.ChatRoomServiceImpl.createChatRoom(..))")
    private void chatCreate() {
    }

    @AfterReturning(pointcut = "partyCreate()", returning = "partyResponse")
    public void publishPartyCreateEvent(PartyResponse partyResponse) {
        if (partyResponse == null) {
            log.warn("Party creation returned null, skipping event publishing.");
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        // 파티 생성 시 주변 10Km 이내의 사용자 찾기
        List<NearbyBookmarkUserDto> nearbyUsers = partyQueryRepository.getUserIdWithDistanceNearbyParty(
                partyResponse.getLatitude(),
                partyResponse.getLongitude(),
                partyResponse.getItemId()
        );

        // 주변 유저가 없으면 알림을 보내지 않음
        if (nearbyUsers.isEmpty()) {
            log.info("10km 이내에 유저가 없습니다. 알림을 건너뜁니다.");
            return;
        }

        // 파티 생성한 유저의 찜한 품목 조회
        List<LikeItemResponse> bookmarkedItems = likeItemQueryRepository.getBookmarkedItems(authUser.getUserId());

        // 찜한 품목이 없으면 알림을 보내지 않음
        if (bookmarkedItems.isEmpty()) {
            log.info("찜한 품목이 없습니다. 알림을 건너뜁니다.");
            return;
        }

        // 메시지 구성
        String message = String.format("%s %s %s 파티가 생성되었습니다.",
                partyResponse.getMarketAddress(),
                partyResponse.getMarketName(),
                partyResponse.getCategory()
        );

        // 공백 제거 후 점으로 변환
        String region = partyResponse.getMarketAddress().trim().replace(" ", ".");

        // 지역에 대해 라우팅 키를 생성하고 메시지 전송
        String routingKey = String.format("party.create.%s", region);
//        rabbitTemplate.convertAndSend(rabbitMqConfig.getExchangeName(), routingKey, message);
        log.info("Message sent to RabbitMQ with routing key: {}", routingKey);

        // 동적으로 큐 생성
        rabbitMqConfig.createQueueWithDLX("party.create", region);
        notificationService.notifyUsersAboutPartyCreation(authUser, partyResponse.getCategory(), partyResponse.getId());
        log.info("Party 생성 알림 전송 완료: {}", partyResponse);
    }


    @AfterReturning(pointcut = "partyCancel()", returning = "partyResponse")
    public void publishPartyCancelEvent(PartyResponse partyResponse) {
        if (partyResponse == null) {
            log.warn("Party cancellation returned null, skipping event publishing.");
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        // 공백 제거 후 점으로 변환
        String region = partyResponse.getMarketAddress().trim().replace(" ", ".");


        // 메시지 구성
        String message = String.format("%s %s %s 파티가 취소되었습니다.",
                partyResponse.getMarketAddress(),
                partyResponse.getMarketName(),
                partyResponse.getCategory()
        );

        // 지역에 대해 라우팅 키를 생성하고 메시지 전송
        String routingKey = String.format("party.cancel.%s", region);
        rabbitTemplate.convertAndSend(rabbitMqConfig.getExchangeName(), routingKey, message);
        log.info("Message sent to RabbitMQ with routing key: {}", routingKey);

        // 동적으로 큐 생성
        rabbitMqConfig.createQueueWithDLX("party.cancel", region);
//        notificationService.notifyUsersAboutPartyCancellation(authUser);
        log.info("Party 취소 알림 전송 완료: 파티: {}", partyResponse);
    }

    @AfterReturning(pointcut = "chatCreate()", returning = "chatRoomResponse")
    public void publishChatCreateEvent(ChatRoomResponse chatRoomResponse) {
        if (chatRoomResponse == null) {
            log.warn("Chat room creation returned null, skipping event publishing.");
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        // 파티 정보를 조회 (예: Repository를 통해 파티 정보 가져오기)
        Party party = validatePartyExists(chatRoomResponse.getPartyId());

        // 공백 제거 후 점으로 변환
        String region = party.getMarketAddress().trim().replace(" ", ".");

        // 메시지 구성
        String message = String.format("%s %s %s 채팅이 취소되었습니다.",
                party.getMarketAddress(),
                party.getMarketName(),
                party.getItem().getCategory()
        );

        // 지역에 대해 라우팅 키를 생성하고 메시지 전송
        String routingKey = String.format("party.cancel.%s", region);
        rabbitTemplate.convertAndSend(rabbitMqConfig.getExchangeName(), routingKey, message);
        log.info("Message sent to RabbitMQ with routing key: {}", routingKey);

        // 동적으로 큐 생성
        rabbitMqConfig.createQueueWithDLX("party.cancel", region);
//        notificationService.notifyUsersAboutPartyChatCreation(authUser, chatRoomResponse);
        log.info("Chat 생성 알림 전송 완료: {}", chatRoomResponse);
    }

    private Party validatePartyExists(Long partyId) {
        return partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));
    }

}

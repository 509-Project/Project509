package com.example.lastproject.aop;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.likeitem.dto.response.LikeItemResponse;
import com.example.lastproject.domain.likeitem.repository.LikeItemQueryRepository;
import com.example.lastproject.domain.party.dto.response.PartyResponse;
import com.example.lastproject.domain.party.repository.PartyQueryRepositoryImpl;
import com.example.lastproject.domain.user.dto.NearbyBookmarkUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitmqAop {

    private final RabbitTemplate rabbitTemplate;
    private final PartyQueryRepositoryImpl partyQueryRepository;
    private final LikeItemQueryRepository likeItemQueryRepository;  // 찜한 품목 조회를 위한 repository 추가

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queues.party.create}")
    private String partyCreateQue;

    @Value("${rabbitmq.queues.party.cancel}")
    private String partyCancelQue;

    @Value("${rabbitmq.queues.chat.create}")
    private String partyChatQue;

    @Value("${rabbitmq.routing-keys.party.create}")
    private String partyCreateKey;

    @Value("${rabbitmq.routing-keys.party.cancel}")
    private String partyCancelKey;

    @Value("${rabbitmq.routing-keys.chat.create}")
    private String chatCreateKey;

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
        List<NearbyBookmarkUserDto> nearbyUsers = partyQueryRepository.getUserIdWithDistanceNearbyParty(partyResponse.getLatitude(), partyResponse.getLongitude(), partyResponse.getItemId());

        if (nearbyUsers.isEmpty()) {
            throw new CustomException(ErrorCode.NO_NEARBY_USERS);
        }

        // 파티 생성한 유저의 찜한 품목 조회
        List<LikeItemResponse> bookmarkedItems = likeItemQueryRepository.getBookmarkedItems(authUser.getUserId());

        // 찜한 품목이 없으면 알림을 보내지 않음
        if (bookmarkedItems.isEmpty()) {
            log.info("찜한 품목이 없습니다. 알림을 건너뜁니다." );
            return;
        }

        // 마켓 주소에서 각 지역 추출 (예: "서울 강남구, 서울 노원구")
        String[] marketRegions = partyResponse.getMarketAddress().split(",");

        // 메시지 생성: "파티 생성 요청\n[마켓 이름] [품목 이름] 파티가 생성되었습니다."
        String message = String.format(
                "파티 생성 요청\n%s %s 파티가 생성되었습니다.",
                partyResponse.getMarketName(),
                partyResponse.getCategory()
        );

        // 각 지역에 대해 라우팅 키를 생성하고 메시지 전송
        for (String region : marketRegions) {
            // 공백 제거 후 점으로 변환
            String routingKey = String.format("party.create.%s", region.trim().replace(" ", "."));
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            log.info("Message sent to RabbitMQ with routing key: {}", routingKey);
        }

        log.info("Party 생성 알림 전송 완료: {}", partyResponse);
    }

    @AfterReturning(pointcut = "partyCancel()")
    public void publishPartyCancelEvent() {
        rabbitTemplate.convertAndSend(exchangeName, partyCancelKey);
        log.info("Message sent to RabbitMQ with routing key: {}", partyCancelKey);
    }

    @AfterReturning(pointcut = "chatCreate()")
    public void publishChatCreateEvent() {
        rabbitTemplate.convertAndSend(exchangeName, chatCreateKey);
        log.info("Message sent to RabbitMQ with routing key: {}", chatCreateKey);
    }


}

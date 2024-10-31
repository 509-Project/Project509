package com.example.lastproject.aop;

import com.example.lastproject.domain.auth.entity.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.notification.service.NotificationService;
import com.example.lastproject.domain.party.dto.response.PartyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class NotificationAop {

    private final NotificationService notificationService;

    // 파티 생성 알림 AOP 메서드
    @AfterReturning(
            pointcut = "@annotation(com.example.lastproject.common.annotation.LogisticsNotify) && " +
                    "execution(com.example.lastproject.domain.party.dto.response.PartyResponse *(..))",
            returning = "result")
    public void afterPartyCreation(Object result) {
        // 반환된 result 객체를 PartyResponse 타입으로 캐스팅
        PartyResponse partyResponse = (PartyResponse) result;

        // SecurityContextHolder에서 AuthUser 가져오기
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 알림 전송 로직
        if (authUser != null) {
            // Party 정보를 PartyResponse에서 가져와 알림 전송
            notificationService.notifyUsersAboutPartyCreation(authUser, partyResponse);
            log.info("Party 생성 알림 전송 완료: {}", partyResponse);


        } else {
            log.warn("알림 전송 실패: 유효한 AuthUser 객체를 찾을 수 없습니다.");
        }
    }

    // 파티 취소 알림 AOP 메서드
//    @AfterReturning(
//            pointcut = "@annotation(com.example.lastproject.common.annotation.LogisticsNotify)", returning = "result")
//    public void afterPartyCancellation(Object result) {
//        // 파라미터에서 마켓 정보를 가져온다 (필요에 따라 조정)
//        Market market = (Market) result; // 메서드의 첫 번째 파라미터가 Market이라 가정
//
//        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        if (authUser != null) {
//            notificationService.notifyUsersAboutPartyCancellation(authUser, market);
//            log.info("파티 취소 알림 전송 완료: 마켓 이름 - {}", market.getMarketName());
//        } else {
//            log.warn("알림 전송 실패: 유효한 AuthUser 객체를 찾을 수 없습니다.");
//        }
//    }

    // 채팅방 생성 알림 AOP 메서드
    @AfterReturning(
            pointcut = "@annotation(com.example.lastproject.common.annotation.LogisticsNotify) && " +
                    "execution(com.example.lastproject.domain.chat.dto.ChatRoomResponse *(..))",
            returning = "result")
    public void afterChatCreation(Object result) {
        // 반환된 result 객체를 PartyResponse 타입으로 캐스팅
        ChatRoomResponse chatRoomResponse = (ChatRoomResponse) result;

        // SecurityContextHolder에서 AuthUser 가져오기
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 알림 전송 로직
        if (authUser != null) {
            // Party 정보를 PartyResponse에서 가져와 알림 전송
            notificationService.notifyUsersAboutPartyChatCreation(authUser, chatRoomResponse);
            log.info("ChatRoom 생성 알림 전송 완료: {}", chatRoomResponse);


        } else {
            log.warn("알림 전송 실패: 유효한 AuthUser 객체를 찾을 수 없습니다.");
        }
    }

}

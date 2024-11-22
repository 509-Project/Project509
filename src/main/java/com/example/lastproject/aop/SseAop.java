//package com.example.lastproject.aop;
//
//import com.example.lastproject.common.dto.AuthUser;
//import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
//import com.example.lastproject.domain.notification.service.NotificationService;
//import com.example.lastproject.domain.party.dto.response.PartyResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@EnableAsync
//@RequiredArgsConstructor
//@Slf4j
//public class SseAop {
//
//    private final NotificationService notificationService;
//
//    @Pointcut("execution(* com.example.lastproject.domain.party.service.PartyService.createParty(..))")
//    private void partyCreate() {
//    }
//
//    @Pointcut("execution(* com.example.lastproject.domain.party.service.PartyService.cancelParty(..))")
//    private void partyCancel() {
//    }
//
//    @Pointcut("execution(* com.example.lastproject.domain.chat.service.ChatRoomServiceImpl.createChatRoom(..))")
//    private void chatCreate() {
//    }
//
//    @AfterReturning(pointcut = "partyCreate()", returning = "partyResponse")
//    public void afterPartyCreation(PartyResponse partyResponse) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AuthUser authUser = (AuthUser) authentication.getPrincipal();
//
//        // Party 정보를 PartyResponse에서 가져와 알림 전송
//        String itemName = partyResponse.getCategory();
//
//        // notifyUsersAboutPartyCreation 메서드 호출 시 marketId 제거
//        notificationService.notifyUsersAboutPartyCreation(authUser, itemName, partyResponse.getId());
//        log.info("Party 생성 알림 전송 완료: {}", partyResponse);
//    }
//
//    // 파티 취소 알림 AOP 메서드
//    @AfterReturning(pointcut = "partyCancel()", returning = "partyResponse")
//    public void afterPartyCancellation(PartyResponse partyResponse) {
//        // 반환된 result 객체를 PartyResponse 타입으로 캐스팅
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AuthUser authUser = (AuthUser) authentication.getPrincipal();
//        notificationService.notifyUsersAboutPartyCancellation(authUser, partyResponse.getId());
//        log.info("Party 취소 알림 전송 완료");
//    }
//
//    // 채팅방 생성 알림 AOP 메서드
//    @AfterReturning(pointcut = "chatCreate()", returning = "chatRoomResponse")
//    public void afterChatCreation(ChatRoomResponse chatRoomResponse) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AuthUser authUser = (AuthUser) authentication.getPrincipal();
//
//        // Party 정보를 PartyResponse에서 가져와 알림 전송
//        notificationService.notifyUsersAboutPartyChatCreation(authUser, chatRoomResponse);
//        log.info("ChatRoom 생성 알림 전송 완료: {}", chatRoomResponse);
//    }
//
//}

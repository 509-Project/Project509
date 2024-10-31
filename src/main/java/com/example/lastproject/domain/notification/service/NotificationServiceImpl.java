package com.example.lastproject.domain.notification.service;

import com.example.lastproject.common.CustomException;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.domain.auth.entity.AuthUser;
import com.example.lastproject.domain.chat.dto.ChatRoomResponse;
import com.example.lastproject.domain.market.entity.Market;
import com.example.lastproject.domain.notification.dto.request.NotificationRequest;
import com.example.lastproject.domain.notification.dto.response.NotificationListResponse;
import com.example.lastproject.domain.notification.dto.response.NotificationResponse;
import com.example.lastproject.domain.notification.entity.Notification;
import com.example.lastproject.domain.notification.entity.NotificationType;
import com.example.lastproject.domain.notification.repository.EmitterRepository;
import com.example.lastproject.domain.notification.repository.NotificationRepository;
import com.example.lastproject.domain.party.dto.response.PartyResponse;
import com.example.lastproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    // 연결 지속시간 한시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private static final String CLIENT_BASIC_URL = "http://localhost:8080";

    /**
     * SSE 연결
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @param lastEventId 클라이언트가 마지막으로 수신한 데이터의 Id값을 의미한다. 이를 이용하여 유실된 데이터를 다시 보내줄 수 있다.
     * @return SseEmitter(발신기)를 생성하여 반환합니다.
     */
    @Transactional
    public SseEmitter subscribe(AuthUser authUser, String lastEventId) {
        String emitterId = makeTimeIncludeId(authUser);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // SseEmitter 의 완료/시간초과/에러로 인한 전송 불가 시 SseEmitter 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 최초 연결시 더미데이터가 없으면 503 오류가 발생하기 때문에 해당 더미 데이터 생성
        String eventId = makeTimeIncludeId(authUser);
        sendToClient(emitter, emitterId, eventId, "연결되었습니다. EventStream Created. [userId=" + authUser.getUserId() + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(authUser.getUserId()));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getKey(), entry.getValue()));
        }
        return emitter;
    }

    /**
     * 데이터 유실 시점을 파악하기 위해 사용자 ID와 현재 시간을 포함한 ID를 생성합니다.
     * @param authUser 인증된 사용자 정보
     * @return 사용자 ID와 현재 시간이 포함된 문자열 ID
     */
    private String makeTimeIncludeId(AuthUser authUser) {
        return authUser.getUserId() + "_" + System.currentTimeMillis();
    }

    /**
     * 클라이언트에게 데이터를 전송합니다.
     * @param emitter SseEmitter 객체
     * @param emitterId 발신기 ID
     * @param eventId 이벤트 ID
     * @param data 전송할 데이터
     * @throws CustomException SSE 연결 오류 발생 시 예외를 던집니다.
     */
    public void sendToClient(SseEmitter emitter, String emitterId, String eventId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name("SSE")
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new CustomException(ErrorCode.SSE_CONNECTION_ERROR);
        }
    }

    /**
     * 알림을 저장하고, 저장된 알림을 클라이언트에게 전송합니다.
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @param request 알림(content, type, url) 요청 정보
     */
    @Override
    public void send(AuthUser authUser, NotificationRequest request) {
        sendNotification(authUser, saveNotification(authUser, request));
    }

    /**
     * 알림 저장
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @param request 알림(content, type, url) 요청 정보
     * @return 새롭게 생성된 알림 정보(id, content, type, enum, url, isRead, createdAt)가 포함된 notification 객체
     */
    @Transactional
    public Notification saveNotification(AuthUser authUser, NotificationRequest request) {
        User user = User.fromAuthUser(authUser);

        Notification notification = Notification.builder()
                .receiver(user)
                .notificationType(request.getNotificationType())
                .content(request.getContent())
                .url(request.getUrl())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        return notification;
    }

    /**
     * 비동기적으로 알림을 전송합니다.
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @param notification 전송할 알림 정보
     */
    @Async
    public void sendNotification(AuthUser authUser, Notification notification) {
        String receiverId = String.valueOf(authUser.getUserId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        // 유저의 모든 SseEmitter 가져옴
        Map<String, SseEmitter> emitters = emitterRepository
                .findAllEmitterStartWithByUserId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장 (유실된 데이터 처리 위함)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, eventId, NotificationResponse.of(notification));
                }
        );
    }

    /**
     * 찜한 품목의 파티가 생성된 경우 알림을 보냅니다.
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @param partyResponse 생성된 파티 정보
     */
    @Override
    @Transactional
    public void notifyUsersAboutPartyCreation(AuthUser authUser, PartyResponse partyResponse) {
        User receiver = User.fromAuthUser(authUser);
        String content = partyResponse.getCategory() + "품목 파티가 생성되었습니다.";

        String redirectUrl = CLIENT_BASIC_URL + "/parties/" + partyResponse.getId();

        NotificationRequest request = NotificationRequest.builder()
                .notificationType(NotificationType.PARTY_CREATE)
                .content(content)
                .url(redirectUrl)
                .receiver(receiver)
                .build();

        send(authUser, request);
    }

    /**
     * 찜한 품목의 파티가 취소된 경우 알림을 보냅니다.
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @param market 취소된 마켓 정보
     */
    @Override
    @Transactional
    public void notifyUsersAboutPartyCancellation(AuthUser authUser, Market market) {
        User receiver = User.fromAuthUser(authUser);
        String content = "참가 신청한 '"+ market.getMarketName() + " 점포' 파티가 취소되었습니다.";

        String redirectUrl = CLIENT_BASIC_URL + "/parties";

        NotificationRequest request = NotificationRequest.builder()
                .notificationType(NotificationType.PARTY_CREATE)
                .content(content)
                .url(redirectUrl)
                .receiver(receiver)
                .build();

        send(authUser, request);
    }

    /**
     * 참가 신청한 파티의 채팅창이 생성된 경우 알림을 보냅니다.
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @param chatRoomResponse 생성된 파티의 채팅창
     */
    @Override
    @Transactional
    public void notifyUsersAboutPartyChatCreation(AuthUser authUser, ChatRoomResponse chatRoomResponse) {
        User receiver = User.fromAuthUser(authUser);
        String content = "참가 신청한 파티의 채팅창이 취소되었습니다.";

        String redirectUrl = CLIENT_BASIC_URL + "/chat/history/" + chatRoomResponse.getId();

        NotificationRequest request = NotificationRequest.builder()
                .notificationType(NotificationType.PARTY_CREATE)
                .content(content)
                .url(redirectUrl)
                .receiver(receiver)
                .build();

        send(authUser, request);
    }

    /**
     * 사용자의 알림 목록을 조회합니다.
     * @param authUser 요청을 보낸 인증된 사용자 정보
     * @return 사용자의 알림 목록을 포함한 NotificationListResponseDto
     */
    @Override
    public NotificationListResponse getNotifications(AuthUser authUser) {
        return NotificationListResponse.of(
                notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(authUser.getUserId()));
    }

    /**
     * 알림을 읽음 처리합니다.
     * @param notificationId 읽음 처리할 알림 ID
     * @param authUser 요청을 보낸 인증된 사용자 정보
     */
    @Override
    @Transactional
    public void readNotification(Long notificationId, AuthUser authUser) {
        Notification notification = findNotification(notificationId);
        notification.read();
        notificationRepository.save(notification);
    }

    /**
     * 알림 삭제
     * @param notificationId 삭제할 알림의 ID
     * @param authUser 요청을 보낸 인증된 사용자 정보
     */
    @Override
    @Transactional
    public void deleteNotification(Long notificationId, AuthUser authUser) {
        notificationRepository.delete(findNotification(notificationId));
    }

    /**
     * 특정 ID의 알림을 조회합니다.
     * @param id 알림의 고유 ID
     * @return CustomException 해당 ID의 알림이 존재하지 않을 경우 발생합니다.
     */
    @Override
    public Notification findNotification(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION));
    }

}

//package com.example.lastproject.domain.notification.service;
//
//import com.example.lastproject.common.dto.AuthUser;
//import com.example.lastproject.common.enums.ErrorCode;
//import com.example.lastproject.common.exception.CustomException;
//import com.example.lastproject.domain.notification.dto.response.NotificationListResponse;
//import com.example.lastproject.domain.notification.entity.Notification;
//import com.example.lastproject.domain.notification.entity.NotificationType;
//import com.example.lastproject.domain.notification.repository.EmitterRepository;
//import com.example.lastproject.domain.notification.repository.NotificationRepository;
//import com.example.lastproject.domain.user.entity.User;
//import com.example.lastproject.domain.user.enums.UserRole;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentCaptor.forClass;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class NotificationServiceImplTest {
//
//    @Spy
//    @InjectMocks
//    private NotificationServiceImpl notificationService;
//
//    @Mock
//    private NotificationRepository notificationRepository;
//
//    @Mock
//    private EmitterRepository emitterRepository;
//
//    @Mock
//    private SseEmitter emitter; // SseEmitter를 Mock으로 설정
//
//    private static final Long DEFAULT_TIMEOUT = 60 * 60 * 1000L;
//
//    private User user;
//    private AuthUser authUser;
//    private Notification notification;
//    String eventId = "eventId";
//    Object data = "test data";
//    String emitterId;
//    String clientBasicUrl;
//
//    @BeforeEach
//    public void setUp() {
//        clientBasicUrl = "http://localhost:8080";
//        authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
//        user = User.fromAuthUser(authUser);
//        emitterId = authUser.getUserId() + "_" + System.currentTimeMillis();
//
//        notification = Notification.builder()
//                .id(1L)
//                .notificationType(null) // 필요한 경우 알맞은 타입으로 설정하세요.
//                .receiver(user)
//                .content("Test notification")
//                .url(null)
//                .isRead(false)
//                .build();
//    }
//
//    @Test
//    public void testSendToClient_Success() throws Exception {
//        // when: 정상적으로 send 메서드 호출
//        notificationService.sendToClient(emitter, emitterId, eventId, data);
//
//        // then: send 메서드가 호출되었는지 검증
//        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
//    }
//
//    @Test
//    public void testSendToClient_IOException() throws Exception {
//
//        // IOException을 발생시키도록 emitter를 모킹
//        doThrow(new IOException("SSE 연결 오류입니다.")).when(emitter).send(any(SseEmitter.SseEventBuilder.class));
//
//        // when & then: CustomException이 발생하는지 확인
//        assertThrows(CustomException.class, () -> notificationService.sendToClient(emitter, emitterId, eventId, data));
//
//        // emitterRepository.deleteById가 호출되었는지 검증
//        verify(emitterRepository).deleteById(emitterId);
//    }
//
//    @Test
//    public void testReadNotification_NotificationNotFound() {
//        // given
//        Long notificationId = 1L;
//        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
//
//        // when & then
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            notificationService.readNotification(notificationId, authUser);
//        });
//
//        assertEquals(ErrorCode.NOT_FOUND_NOTIFICATION, exception.getErrorCode());
//        verify(notificationRepository).findById(notificationId);
//    }
//
//    @Test
//    public void testReadNotification_NoAccess() {
//        // given
//        Long notificationId = 1L;
//
//        // when & then
//        assertThrows(CustomException.class, () -> {
//            notificationService.readNotification(notificationId, authUser);
//        });
//
//        // Verify the access check method here if applicable
//        verify(notificationRepository).findById(notificationId);
//    }
//
//    @Test
//    public void test클라이언트에전송_입출력예외발생() throws IOException {
//        // When
//        notificationService.sendToClient(emitter, emitterId, eventId, data);
//
//        // Then
//        ArgumentCaptor<SseEmitter.SseEventBuilder> captor = forClass(SseEmitter.SseEventBuilder.class);
//        verify(emitter).send(captor.capture());
//
//        // SseEventBuilder의 데이터를 직접 검증하는 방법
//        SseEmitter.SseEventBuilder capturedEvent = captor.getValue();
//
//        // 여기서는 비교할 더미 이벤트 빌더를 생성해야 합니다.
//        // 그러나 내부 상태에 직접 접근할 수 없기 때문에 전송된 데이터를 검증할 것입니다.
//        // capturedEvent가 예상된 데이터를 포함하는지 확인하기 위해 해당 메서드를 간접적으로 사용하여 테스트할 수 있습니다.
//
//        assertNotNull(capturedEvent);  // check that something was captured
//
//        // 확인하기 위한 테스트 대체 (실제적으로 동작하지 않지만)
//        // Mockito는 빌더에 대한 직접 필드 접근을 허용하지 않으므로, 이렇게 할 수 없습니다.
//        // 대신, 통합 테스트를 위해 코드의 실제 흐름에 의존해야 합니다.
//    }
//
//
//
//    @Test
//    public void 알림이_정상적으로_전송된다() {
//        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
//
//        // when
//        notificationService.send(authUser, notification);
//
//        // then
//        verify(notificationRepository, times(1)).save(any(Notification.class));
//    }
//
//    @Test
//    public void 알림이_정상적으로_생성된다() {
//        // given
//        User receiver = user;
//
//        // when
//        notification = Notification.builder()
//                .id(1L)
//                .notificationType(NotificationType.PARTY_CREATE) // 필요한 경우 알맞은 타입으로 설정하세요.
//                .receiver(receiver)
//                .content("알림 내용")
//                .url("http://example.com")
//                .isRead(false)
//                .build();
//
//        // then
//        assertNotNull(notification);
//        assertEquals(NotificationType.PARTY_CREATE, notification.getNotificationType());
//        assertEquals(receiver, notification.getReceiver());
//        assertEquals("알림 내용", notification.getContent());
//        assertEquals("http://example.com", notification.getUrl());
//        assertFalse(notification.isRead());
//    }
//
//    @Test
//    public void test알림조회() {
//        // Given: mock 데이터를 설정합니다.
//        when(notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(anyLong()))
//                .thenReturn(Collections.singletonList(notification));
//
//        // When: 알림 목록을 조회합니다.
//        NotificationListResponse result = notificationService.getNotifications(authUser);
//
//        // Then: 결과를 검증합니다.
//        assertNotNull(result);
//        assertEquals(1, result.getNotifications().size());
//        assertEquals(notification.getContent(), result.getNotifications().get(0).getContent());
//        verify(notificationRepository, times(1)).findAllByReceiverIdOrderByCreatedAtDesc(authUser.getUserId());
//    }
//
//    @Test
//    public void 알림을_읽으면_isRead가_변경된다() {
//        // when
//        notification.read();
//
//        // then
//        assertTrue(notification.isRead());
//    }
//
//
//    @Test
//    public void 알림ID가_없을_시_예외가_발생한다() {
//        // given
//        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(CustomException.class,
//                () -> notificationService.deleteNotification(1L, authUser),
//                "예상된 예외가 발생하지 않았습니다.");
//
//        // 예외 코드가 NOT_FOUND_NOTIFICATION인지 검증
//        try {
//            notificationService.deleteNotification(1L, authUser);
//        } catch (CustomException e) {
//            assertEquals(ErrorCode.NOT_FOUND_NOTIFICATION, e.getErrorCode(), "존재하지 않는 알림입니다.");
//        }
//    }
//
//    @Test
//    public void 알림읽기_권한없음_예외처리() {
//        // given
//        Long notificationId = 1L;
//        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
//
//        // 다른 사용자의 알림을 가져오도록 설정
//        AuthUser differentUser = new AuthUser(2L, "other@example.com", UserRole.ROLE_USER);
//
//        // when, then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> notificationService.readNotification(notificationId, differentUser));
//
//        assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
//        verify(notificationRepository, times(1)).findById(notificationId);
//    }
//
//    @Test
//    public void 알림읽기_알림없음_예외처리() {
//        // given
//        Long notificationId = 1L;
//        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
//
//        // when, then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> notificationService.readNotification(notificationId, authUser));
//
//        assertEquals(ErrorCode.NOT_FOUND_NOTIFICATION, exception.getErrorCode());
//        verify(notificationRepository, times(1)).findById(notificationId);
//    }
//
//    @Test
//    public void 알림삭제_권한없음_예외처리() {
//        // given
//        Long notificationId = 1L;
//        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
//
//        // 다른 사용자의 알림을 가져오도록 설정
//        AuthUser differentUser = new AuthUser(2L, "other@example.com", UserRole.ROLE_USER);
//
//        // when, then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> notificationService.deleteNotification(notificationId, differentUser));
//
//        assertEquals(ErrorCode.UNAUTHORIZED_ACCESS, exception.getErrorCode());
//        verify(notificationRepository, times(1)).findById(notificationId);
//    }
//
//    @Test
//    public void 알림삭제_알림없음_예외처리() {
//        // given
//        Long notificationId = 1L;
//        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
//
//        // when, then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> notificationService.deleteNotification(notificationId, authUser));
//
//        assertEquals(ErrorCode.NOT_FOUND_NOTIFICATION, exception.getErrorCode());
//        verify(notificationRepository, times(1)).findById(notificationId);
//    }
//
//    @Test
//    public void subscribe_예외발생_테스트() {
//        // given
//        SseEmitter mockEmitter = new SseEmitter(DEFAULT_TIMEOUT);
//
//        // save 메서드가 예외를 던지도록 설정
//        doThrow(new RuntimeException("Save operation failed")).when(emitterRepository).save(anyString(), any(SseEmitter.class));
//
//        // when / then
//        assertThrows(RuntimeException.class, () -> {
//            notificationService.subscribe(authUser, "");
//        });
//
//        // verify: save 메서드가 호출되었는지 확인
//        verify(emitterRepository).save(anyString(), any(SseEmitter.class));
//    }
//
//
//    @Test
//    public void notifyUsersAboutPartyCreation_성공적으로_알림을_보내야_함() {
//        // given
//        String itemName = "테스트 품목";
//        Long partyId = 1L;
//
//        // when
//        notificationService.notifyUsersAboutPartyCreation(authUser, itemName, partyId);
//
//        // then
//        verify(notificationRepository, times(1)).save(any(Notification.class));
//    }
//
//    @Test
//    public void notifyUsersAboutPartyCancellation_성공적으로_알림을_보내야_함() {
//        // when
//        notificationService.notifyUsersAboutPartyCancellation(authUser);
//
//        // then
//        verify(notificationRepository, times(1)).save(any(Notification.class));
//    }
//
//    @Test
//    public void notifyUsersAboutPartyCreation_예외발생시_알림_저장_실패() {
//        // given
//        String itemName = "테스트 품목";
//        Long partyId = 1L;
//
//        // notificationRepository의 save 메서드가 예외를 던지도록 설정
//        doThrow(new RuntimeException("Notification save failed"))
//                .when(notificationRepository).save(any(Notification.class));
//
//        // when / then
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            notificationService.notifyUsersAboutPartyCreation(authUser, itemName, partyId);
//        });
//
//        // 예외 메시지 검증
//        assertEquals("Notification save failed", exception.getMessage());
//    }
//
//}

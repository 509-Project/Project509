package com.example.lastproject.domain.notification.controller;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.config.JwtAuthenticationToken;
import com.example.lastproject.config.JwtUtil;
import com.example.lastproject.config.SecurityConfig;
import com.example.lastproject.domain.notification.dto.response.NotificationListResponse;
import com.example.lastproject.domain.notification.entity.Notification;
import com.example.lastproject.domain.notification.service.NotificationService;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtUtil.class})
class NotificationControllerTest {

    // Spring Batch에서 사용하는 빈을 Mock 처리하여 테스트에 영향을 주지 않도록 설정합니다.
    @MockBean
    private JobRepository jobRepository;

    // Spring Batch에서 사용하는 빈을 Mock 처리하여 테스트에 영향을 주지 않도록 설정합니다.
    @MockBean
    private JobExplorer jobExplorer;

    // Spring Batch에서 사용하는 빈을 Mock 처리하여 테스트에 영향을 주지 않도록 설정합니다.
    @MockBean
    private JobOperator jobOperator;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private User user;
    private AuthUser authUser;

    @BeforeEach
    public void setUp() {
        authUser = new AuthUser(1L, "test@email.com", UserRole.ROLE_USER);
        user = User.fromAuthUser(authUser);
    }

    @Test
    public void 알림목록_조회_테스트() throws Exception {
        List<Notification> notificationList = List.of(
                Notification.builder()
                        .id(1L)
                        .notificationType(null)
                        .receiver(new User(/* receiver 설정 */))
                        .content("첫 번째 알림 내용")
                        .url("http://example.com/notification/1")
                        .isRead(false)
                        .build(),
                Notification.builder()
                        .id(2L)
                        .notificationType(null)
                        .receiver(new User(/* receiver 설정 */))
                        .content("두 번째 알림 내용")
                        .url("http://example.com/notification/2")
                        .isRead(true)
                        .build()
        );

        NotificationListResponse response = NotificationListResponse.of(notificationList);

        when(notificationService.getNotifications(authUser)).thenReturn(response);

        mockMvc.perform(get("/notifications")
                        .with(authentication(new JwtAuthenticationToken(authUser))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifications").isNotEmpty());
    }

    @Test
    public void 알림읽음_변경_테스트() throws Exception {
        Long notificationId = 1L;

        mockMvc.perform(patch("/notifications/{notificationId}", notificationId)
                        .with(authentication(new JwtAuthenticationToken(authUser))))
                .andExpect(status().isOk())
                .andExpect(content().string("알림이 읽음으로 변경되었습니다."));

        verify(notificationService).readNotification(notificationId, authUser);
    }

    @Test
    public void 알림삭제_테스트() throws Exception {
        Long notificationId = 1L;

        mockMvc.perform(delete("/notifications/{notificationId}", notificationId)
                        .with(authentication(new JwtAuthenticationToken(authUser))))
                .andExpect(status().isOk())
                .andExpect(content().string("알림이 성공적으로 삭제되었습니다."));

        verify(notificationService).deleteNotification(notificationId, authUser);
    }

    @Test
    public void SSE_연결_테스트() throws Exception {
        AuthUser authUser = new AuthUser(1L, "user@example.com", UserRole.ROLE_USER);
        String lastEventId = "someEventId";
        SseEmitter emitter = new SseEmitter();

        when(notificationService.subscribe(authUser, lastEventId)).thenReturn(emitter);

        mockMvc.perform(get("/notifications/connect")
                        .header("Last-Event-ID", lastEventId)
                        .with(authentication(new JwtAuthenticationToken(authUser))))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted());
    }


}
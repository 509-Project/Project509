//package com.example.lastproject.domain.user.service;
//
//import com.example.lastproject.common.dto.AuthUser;
//import com.example.lastproject.common.enums.ErrorCode;
//import com.example.lastproject.common.exception.CustomException;
//import com.example.lastproject.domain.auth.dto.request.SignupRequest;
//import com.example.lastproject.domain.penalty.repository.PenaltyRepository;
//import com.example.lastproject.domain.user.dto.response.UserResponse;
//import com.example.lastproject.domain.user.entity.User;
//import com.example.lastproject.domain.user.enums.UserRole;
//import com.example.lastproject.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PenaltyRepository penaltyRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private AuthUser authUser;
//    private User user;
//    private Long userId;
//    private SignupRequest signupRequest;
//    private UserResponse userResponse;
//    private String rowPassword;
//    private String encodedPassword;
//    private String nickname;
//
//    @BeforeEach
//    void setUp() {
//
//        rowPassword = "123456!A";
//        encodedPassword = passwordEncoder.encode(rowPassword);
//
//        signupRequest = new SignupRequest(
//                "1@1",
//                rowPassword,
//                "개발자1",
//                "사하구 사하로",
//                UserRole.ROLE_ADMIN.toString()
//        );
//
//        authUser = new AuthUser(
//                1L,
//                "1@1",
//                UserRole.ROLE_ADMIN
//        );
//
//        userId = 1L;
////        user = User.fromAuthUser(authUser);
//
//    }
//
//    @Nested
//    class GetUser {
//
//        @Test
//        void 유저_조회_성공() {
//
//        }
//
//        @Nested
//        class GetUser_Failed {
//
//            @Test
//            void 존재하지_않는_유저여서_조회_실패() {
//
//                given(userRepository.findById(anyLong())).willReturn(Optional.empty());
//
//                CustomException exception = assertThrows(CustomException.class, () -> {
//                    userService.getUser(anyLong());
//                });
//
//                assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//            }
//
//        }
//
//    }
//
//    @Nested
//    class ChangePassword {
//
//        @Test
//        void 비밀번호변경_성공() {
//
//        }
//
//        @Nested
//        class ChangePassword_Failed {
//
//            @Test
//            void 기존비밀번호와_새비밀번호가_같아서_변경실패() {
//
//            }
//
//            @Test
//            void 기존비밀번호가_틀려서_변경실패() {
//
//            }
//        }
//    }
//
//    @Nested
//    class UpdateUserDetails {
//    }
//}
//package com.example.lastproject.domain.auth.service;
//
//import com.example.lastproject.common.dto.AuthUser;
//import com.example.lastproject.common.enums.ErrorCode;
//import com.example.lastproject.common.exception.CustomException;
//import com.example.lastproject.config.JwtUtil;
//import com.example.lastproject.domain.auth.dto.request.SigninRequest;
//import com.example.lastproject.domain.auth.dto.request.SignupRequest;
//import com.example.lastproject.domain.auth.dto.response.SignupResponse;
//import com.example.lastproject.domain.user.entity.User;
//import com.example.lastproject.domain.user.enums.UserRole;
//import com.example.lastproject.domain.user.enums.UserStatus;
//import com.example.lastproject.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @InjectMocks
//    private AuthServiceImpl authService;
//
//    private User user;
//    private Long userId;
//    private AuthUser authUser;
//    private SignupRequest signupRequest;
//    private SignupResponse signupResponse;
//    private SigninRequest signinRequest;
//    private String token;
//    private String rowPassword;
//    private String encodedPassword;
//
//
//    @BeforeEach
//    void setUp() {
//
//        rowPassword = "123456!A";
//
//        signupRequest = new SignupRequest(
//                "1@1",
//                rowPassword,
//                "개발자1",
//                "사하구 사하로",
//                UserRole.ROLE_ADMIN.toString()
//        );
//
//        encodedPassword = passwordEncoder.encode(rowPassword);
//
//        userId = 1L;
//        user = new User(
//                signupRequest.getEmail(),
//                encodedPassword,
//                signupRequest.getNickname(),
//                signupRequest.getAddress(),
//                UserRole.ROLE_ADMIN
//        );
//
//        authUser = new AuthUser(
//                1L,
//                "1@1",
//                UserRole.ROLE_ADMIN
//        );
//
//        signinRequest = new SigninRequest(
//                "1@1",
//                "123456!A"
//        );
//
//        signupResponse = new SignupResponse("개발자1");
//
//        token = jwtUtil.createToken(
//                1L,
//                "1@1",
//                UserRole.ROLE_ADMIN
//        );
//
//    }
//
//    @Nested
//    class Signup {
//
//        @Test
//        void 중복된_이메일이_있으면_가입실패() {
//
//            // given
//            given(userRepository.existsByEmail("1@1")).willReturn(true);
//
//            // when - then
//            CustomException exception = assertThrows(CustomException.class, () -> {
//                authService.signup(signupRequest);
//            });
//            assertEquals(ErrorCode.EXIST_EMAIL, exception.getErrorCode());
//
//        }
//
//        @Test
//        void 중복된_닉네임이_있으면_가입실패() {
//
//            // given
//            given(userRepository.existsByNickname("개발자1")).willReturn(true);
//
//            // when - then
//            CustomException exception = assertThrows(CustomException.class, () -> {
//                authService.signup(signupRequest);
//            });
//            assertEquals(ErrorCode.EXIST_NICKNAME, exception.getErrorCode());
//        }
//
//        @Test
//        void 비밀번호_인코딩값과_달라서_가입실패() {
//
//            given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
//            given(userRepository.existsByNickname(signupRequest.getNickname())).willReturn(false);
//            given(passwordEncoder.encode(signupRequest.getPassword())).willReturn("");
//
//            // When & Then
//            CustomException exception = assertThrows(CustomException.class, () ->
//                    authService.signup(signupRequest));
//            assertEquals(ErrorCode.ENCODING_FAILED, exception.getErrorCode());
//
//        }
//
//    }
//
//    @Nested
//    class Login {
//
//        @Test
//        void 유저가_존재하면_로그인_성공() {
//
//            given(userRepository.findByEmail("1@1")).willReturn(Optional.of(user));
//            given(passwordEncoder.matches(any(), any())).willReturn(true);
//
//            assertEquals(authService.signin(signinRequest), token);
//        }
//
//        @Nested
//        class Login_Failed {
//
//            @Test
//            void 비밀번호가_틀려서_로그인_실패() {
//
//                given(userRepository.findByEmail("1@1")).willReturn(Optional.of(user));
//                given(passwordEncoder.matches(any(), any())).willReturn(false);
//
//                CustomException exception = assertThrows(CustomException.class, () -> {
//                    authService.signin(signinRequest);
//                });
//
//                assertEquals(ErrorCode.SIGN_IN_ERROR, exception.getErrorCode());
//            }
//
//            @Test
//            void 유저가_존재하지_않아_로그인실패() {
//
//                // given
//                given(userRepository.findByEmail("1@1")).willReturn(Optional.empty());
//
//                // when - then
//                CustomException exception = assertThrows(CustomException.class, () -> {
//                    authService.signin(signinRequest);
//                });
//                assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//            }
//        }
//    }
//
//    @Nested
//    class Withdrawal {
//
//        @Test
//        void 유저가_존재하면_회원탈퇴_성공() {
//
//            given(userRepository.findById(userId)).willReturn(Optional.of(user));
//            authService.withdrawal(authUser);
//
//            assertEquals(UserStatus.DELETED, user.getUserStatus());
//
//        }
//
//        @Nested
//        class Withdrawal_Failed {
//
//            @Test
//            void 유저가_존재하지않아_회원탈퇴_실패() {
//                given(userRepository.findById(1L)).willReturn(Optional.empty());
//
//                CustomException exception = assertThrows(CustomException.class, () -> {
//                    authService.withdrawal(authUser);
//                });
//                assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//            }
//
//            @Test
//            void 이미_탈퇴한_유저는_회원탈퇴_실패() {
//
//                ReflectionTestUtils.setField(user, "userStatus", UserStatus.DELETED);
//
//                given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
//
//                CustomException exception = assertThrows(CustomException.class, () -> {
//                    authService.withdrawal(authUser);
//                });
//                assertEquals(ErrorCode.WITHDRAWAL_ERROR, exception.getErrorCode());
//
//            }
//        }
//    }
//
//}

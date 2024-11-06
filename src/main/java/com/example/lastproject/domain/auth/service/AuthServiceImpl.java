package com.example.lastproject.domain.auth.service;

import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.config.JwtUtil;
import com.example.lastproject.domain.auth.dto.request.SigninRequest;
import com.example.lastproject.domain.auth.dto.request.SignupRequest;
import com.example.lastproject.domain.auth.dto.response.SignupResponse;
import com.example.lastproject.domain.auth.dto.response.WithdrawalResponse;
import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.enums.UserRole;
import com.example.lastproject.domain.user.enums.UserStatus;
import com.example.lastproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     *
     * @param signupRequest 회원가입 시 필요한 json body ( 이메일, 비밀번호, 닉네임, 주소, 권한 )
     * @return responseDTO ( "${닉네임} 님의 회원가입이 완료되었습니다" )
     */
    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new CustomException(ErrorCode.EXIST_EMAIL);
        }

        if (userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new CustomException(ErrorCode.EXIST_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
                signupRequest.getEmail(),
                encodedPassword,
                signupRequest.getNickname(),
                signupRequest.getAddress(),
                userRole
        );

        User savedUser = userRepository.save(newUser);
        return new SignupResponse(savedUser.getNickname());

    }

    /**
     * 로그인
     *
     * @param signinRequest 로그인 시 필요한 json body ( 이메일, 비밀번호 )
     * @return 로그인 후 발급되는 토큰 반환 ( "Bearer eyJ~" )
     */
    @Override
    public String signin(SigninRequest signinRequest) {

        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.SIGN_IN_ERROR);
        }

        return jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

    }

    /**
     * 회원 탈퇴
     *
     * @param authUser 로그인한 사용자
     */
    @Override
    @Transactional
    public WithdrawalResponse withdrawal(AuthUser authUser) {

        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new CustomException(ErrorCode.WITHDRAWAL_ERROR);
        }

        user.toggleDelete();
        return new WithdrawalResponse(user.getNickname());

    }

}

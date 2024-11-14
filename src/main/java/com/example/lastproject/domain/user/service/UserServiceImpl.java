package com.example.lastproject.domain.user.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.common.enums.CustomMessage;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.user.dto.request.UserChangePasswordRequest;
import com.example.lastproject.domain.user.dto.request.UserUpdateRequest;
import com.example.lastproject.domain.user.dto.response.UserResponse;
import com.example.lastproject.domain.user.entity.User;
import com.example.lastproject.domain.user.enums.UserStatus;
import com.example.lastproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PenaltyCountService penaltyCountService;

    /**
     * 사용자 조회
     *
     * @param userId 조회할 사용자 id
     * @return 응답 객체
     */
    @Override
    public UserResponse getUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if (user.getUserStatus() == (UserStatus.DELETED)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        penaltyCountService.setPenaltyCount(userId);

        int penaltyCount = Integer.parseInt(
                penaltyCountService.getPenaltyCount(user.getId())
        );

        String emoji = (penaltyCount >= 3) ? "👻" : "⭐";

        return new UserResponse(
                user.getEmail(),
                emoji + user.getNickname(),
                CustomMessage.USER_FOUND.getMessage()
        );
    }

    /**
     * 비밀번호 변경
     *
     * @param authUser 비밀번호 변경할 사용자 id
     * @param request  비밀번호 변경에 필요한 정보 ( 기존 비밀번호, 새 비밀번호 )
     * @return response 객체 ( email, nickname, "_ 님이 조회되었습니다." )
     */
    @Override
    @Transactional
    public UserResponse changePassword(
            AuthUser authUser,
            UserChangePasswordRequest request
    ) {

        User user = User.fromAuthUser(authUser);

        if (user.getUserStatus() == (UserStatus.DELETED)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR);
        }

        // 새 비밀번호와 기존 비밀번호가 같은지 확인
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));

        return new UserResponse(
                user.getEmail(),
                user.getNickname(),
                CustomMessage.CHANGE_PW_SUCCESS.getMessage()
        );
    }

    /**
     * 사용자 정보 변경
     *
     * @param authUser 사용자 정보를 변경할 사용자 id
     * @param request  사용자 정보 변경에 필요한 정보 ( email, nickname, address )
     * @return response 객체 ( email, nickname, "_ 님이 조회되었습니다." )
     */
    @Override
    @Transactional
    public UserResponse updateUser(
            AuthUser authUser,
            UserUpdateRequest request
    ) {

        User user = User.fromAuthUser(authUser);

        if (user.getUserStatus() == (UserStatus.DELETED)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if (request.getNickname() == null && request.getAddress() == null) {
            throw new CustomException(ErrorCode.NO_CONTENTS);
        }

        user.update(request);
        userRepository.save(user);
        return new UserResponse(
                user.getEmail(),
                user.getNickname(),
                CustomMessage.CHANGE_DETAIL_SUCCESS.getMessage()
        );
    }

}

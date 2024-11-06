package com.example.lastproject.domain.user.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.user.dto.request.UserChangePasswordRequest;
import com.example.lastproject.domain.user.dto.request.UserUpdateRequest;
import com.example.lastproject.domain.user.dto.response.UserResponse;

public interface UserService {

    UserResponse getUser(Long userId);

    UserResponse changePassword(AuthUser authUser, UserChangePasswordRequest request);

    UserResponse updateUser(AuthUser authUser, UserUpdateRequest request);

}

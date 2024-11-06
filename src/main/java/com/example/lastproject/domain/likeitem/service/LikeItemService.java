package com.example.lastproject.domain.likeitem.service;

import com.example.lastproject.common.dto.AuthUser;
import com.example.lastproject.domain.likeitem.dto.response.LikeItemResponse;

import java.util.List;

public interface LikeItemService {

    // 즐겨찾기 추가
    void bookmarkItem(AuthUser authUser, Long itemId);

    // 즐겨찾기 리스트 조회
    List<LikeItemResponse> getLikeItems(AuthUser authUser);

    // 즐겨찾기 삭제
    void deleteLikeItem(AuthUser authUser, Long likeItemId);

}

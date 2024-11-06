package com.example.lastproject.domain.likeitem.repository;

import com.example.lastproject.domain.likeitem.dto.response.LikeItemResponse;

import java.util.List;

public interface LikeItemQueryRepository {

    List<LikeItemResponse> getBookmarkedItems(long userId);

    boolean existsByUserBookmarkedItem(long userId, long itemId);

}

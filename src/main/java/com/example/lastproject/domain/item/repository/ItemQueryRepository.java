package com.example.lastproject.domain.item.repository;

import com.example.lastproject.domain.item.dto.response.ItemResponse;

import java.util.List;

public interface ItemQueryRepository {

    List<ItemResponse> searchItemsByKeywordInCategory(String keyword);

}

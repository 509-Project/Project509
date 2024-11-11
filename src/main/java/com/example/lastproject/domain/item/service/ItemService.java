package com.example.lastproject.domain.item.service;

import com.example.lastproject.domain.item.dto.response.ItemResponse;
import com.example.lastproject.domain.item.entity.Item;

import java.util.List;

public interface ItemService {

    // 품목 조회
    List<ItemResponse> searchItems(String keyword);

    // 품목 엔티티 검증 및 호출
    Item validateItem(Long itemId);

}

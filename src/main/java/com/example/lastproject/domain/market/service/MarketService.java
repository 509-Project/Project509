package com.example.lastproject.domain.market.service;

import com.example.lastproject.domain.market.dto.request.MarketRequestDto;

public interface MarketService {

    // 마켓 저장
    void saveMarket(MarketRequestDto requestDto);

    // 마켓 삭제
    void deleteMarket(Long marketId);

}

package com.example.lastproject.domain.market.service;

import com.example.lastproject.domain.market.dto.request.MarketRequestDto;
import com.example.lastproject.domain.market.dto.response.AddressResponseDto;

import java.util.List;

public interface MarketService {

    // 마켓 저장
    void saveMarket(MarketRequestDto requestDto);

    // 마켓 삭제
    void deleteMarket(Long marketId);

    // 주소 검색
    List<AddressResponseDto> searchAddress(String keyword);
}

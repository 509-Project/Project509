package com.example.lastproject.domain.market.service;

import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.market.dto.request.MarketRequestDto;
import com.example.lastproject.domain.market.entity.Market;
import com.example.lastproject.domain.market.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketServiceImpl implements MarketService {

    private final MarketRepository marketRepository;

    // 마켓 저장
    @Transactional
    public void saveMarket(MarketRequestDto requestDto) {
        marketRepository.save(requestDto.toEntity());
    }

    // 마켓 삭제
    @Transactional
    public void deleteMarket(Long marketId) {

        Market market = marketRepository.findById(marketId).orElseThrow(
                () -> new CustomException(ErrorCode.MARKET_NOT_FOUND)
        );

        marketRepository.delete(market);
    }

}

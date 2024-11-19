package com.example.lastproject.domain.market.service;

import com.example.lastproject.common.exception.CustomException;
//import com.example.lastproject.domain.market.dto.request.MarketRequestDto;
import com.example.lastproject.domain.market.entity.Market;
import com.example.lastproject.domain.market.repository.MarketRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MarketServiceTest {

    @Mock
    MarketRepository marketRepository;

    @InjectMocks
    MarketServiceImpl marketService;

//    @Test
//    @DisplayName("마켓 저장 테스트")
//    void 마켓이_정상적으로_등록된다() {
//
//        // given
//        MarketRequestDto requestDto = new MarketRequestDto();
//
//        // when
//        marketService.saveMarket(requestDto);
//
//        // then
//        verify(marketRepository, times(1)).save(any(Market.class));
//    }

//    @Test
//    @DisplayName("마켓 삭제 테스트")
//    void 마켓이_정상적으로_삭제된다() {
//
//        // given
//        Long marketId = 1L;
//        Market market = new Market("치킨집", "서울");
//        given(marketRepository.findById(marketId)).willReturn(Optional.of(market));
//
//        // when
//        marketService.deleteMarket(marketId);
//
//        // then
//        verify(marketRepository, times(1)).delete(market);
//    }

    @Test
    @DisplayName("마켓 조회 테스트")
    void 삭제할_마켓이_없으면_예외를_반환한다() {

        // given
        Long marketId = 1L;
        given(marketRepository.findById(marketId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> marketService.deleteMarket(marketId));

        // then
        assertEquals("마켓 정보를 찾을 수 없습니다.", exception.getMessage());
    }

}

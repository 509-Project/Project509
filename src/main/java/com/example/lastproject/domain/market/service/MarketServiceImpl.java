package com.example.lastproject.domain.market.service;

import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.market.dto.request.MarketRequestDto;
import com.example.lastproject.domain.market.dto.response.AddressResponseDto;
import com.example.lastproject.domain.market.entity.Market;
import com.example.lastproject.domain.market.repository.MarketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketServiceImpl implements MarketService {

    private final ObjectMapper objectMapper;
    private final MarketRepository marketRepository;

    @Qualifier("kakaoApi")
    private final WebClient webClient;

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

    // 주소 검색
    public List<AddressResponseDto> searchAddress(String keyword) {

        // 주소 검색 api 요청
        String jsonData = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        // 검색 파라미터값
                        .queryParam("query", keyword)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            // 필요한 배열만 추출
            List<AddressResponseDto> responseDtoList = new ArrayList<>();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            ArrayNode arrayNode = (ArrayNode) jsonNode.get("documents");

            //추출한 배열에서 필요한 필드값만 추출후 매핑후 DTO 생성
            if (!arrayNode.isEmpty()) {
                for (JsonNode value : arrayNode) {
                    String address = value.get("address_name").asText();
                    String longitude = value.get("x").asText();
                    String latitude = value.get("y").asText();

                    responseDtoList.add(new AddressResponseDto(address, latitude, longitude));
                }
                return responseDtoList;
            } else {
                throw new CustomException(ErrorCode.ADDRESS_NOT_FOUND);
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.API_PARSE_ERROR);
        }
    }

}

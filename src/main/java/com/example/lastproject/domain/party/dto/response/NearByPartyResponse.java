package com.example.lastproject.domain.party.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class NearByPartyResponse {

    private final long id;
    private final String marketName;
    private final String marketAddress;
    private final String locationRange;
    private final long itemId;

    @QueryProjection
    public NearByPartyResponse(Long id, String marketName, String marketAddress, BigDecimal locationRange, Long itemId) {
        this.id = id;
        this.marketName = marketName;
        this.marketAddress = marketAddress;
        this.locationRange = locationRange.setScale(1, RoundingMode.HALF_UP) + "km";
        this.itemId = itemId;
    }

}

package com.example.lastproject.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class NearbyBookmarkUserDto {

    private final long userId;
    private final String locationRange;

    @QueryProjection
    public NearbyBookmarkUserDto(Long userId, BigDecimal locationRange) {
        this.userId = userId;
        this.locationRange = locationRange.setScale(1, RoundingMode.HALF_UP) + "km";
    }

}

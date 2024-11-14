package com.example.lastproject.domain.market.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddressResponseDto {
    private final String address;
    private final String latitude;
    private final String longitude;
}

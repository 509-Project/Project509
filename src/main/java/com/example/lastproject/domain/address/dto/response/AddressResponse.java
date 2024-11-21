package com.example.lastproject.domain.address.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddressResponse {
    private final String address;
    private final String latitude;
    private final String longitude;
}

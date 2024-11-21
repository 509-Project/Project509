package com.example.lastproject.domain.address.service;

import com.example.lastproject.domain.address.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    // 주소 검색
    List<AddressResponse> searchAddress(String keyword);
}

package com.example.lastproject.domain.address.controller;

import com.example.lastproject.domain.address.dto.response.AddressResponse;
import com.example.lastproject.domain.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * @param keyword 주소 검색단어
     * @return 검색결과 : 주소, 위도, 경도
     */
    @GetMapping
    public ResponseEntity<List<AddressResponse>> searchAddress(@RequestParam String keyword) {
        List<AddressResponse> responseDto = addressService.searchAddress(keyword);
        return ResponseEntity.ok(responseDto);
    }

}

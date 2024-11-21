package com.example.lastproject.domain.address.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @InjectMocks
    AddressServiceImpl marketService;

    @Mock
    WebClient webClient;

    @Test
    @DisplayName("주소 검색 테스트")
    void 주소가_정상적으로_반환된다() {


    }

}

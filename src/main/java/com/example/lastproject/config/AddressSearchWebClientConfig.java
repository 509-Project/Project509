package com.example.lastproject.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AddressSearchWebClientConfig {

    @Value("${KAKAO_URL:}")
    private String baseUrl;

    @Value("${KAKAO_API_KEY:}")
    private String defaultHeader;

    @Bean
    @Qualifier("kakaoApi")
    public WebClient kakaoApi(){
        return WebClient.builder()
                .defaultHeader("Authorization",defaultHeader)
                .baseUrl(baseUrl)
                .build();
    }

}

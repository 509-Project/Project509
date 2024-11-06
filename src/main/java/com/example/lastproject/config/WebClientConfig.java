package com.example.lastproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 환경변수 설정값이 없으면 빈 문자열을 주입
    @Value("${ITEM_API_KEY:}")
    private String apiKey;

    @Value("${API_PORT:}")
    private String apiPort;

    @Value("${API_URL:}")
    private String apiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        // 웹 큻라이언트 요청시 최대 메모리 저장용량을 10MB로 설정
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                        .build())
                // 기본 url 설정
                .baseUrl(apiPort + "/" + apiKey + "/json" + "/" + apiUrl)
                .build();
    }

}

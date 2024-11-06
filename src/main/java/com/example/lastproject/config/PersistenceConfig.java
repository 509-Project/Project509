package com.example.lastproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class PersistenceConfig {
    // @EnableJpaAuditing 의 적절한 관심사 분리를 위해 생성된 클래스
}
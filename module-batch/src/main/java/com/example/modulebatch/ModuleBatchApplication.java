package com.example.modulebatch;

import com.example.lastproject.config.*;
import com.example.modulebatch.batchconfig.WebClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 가져와야할 빈과 걸러야할 빈 등록
 */
@ComponentScan(
        basePackageClasses = {QueryDslConfig.class, PersistenceConfig.class, WebClientConfig.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes =
                        {       // 인증관련 빈등록 필터링
                                JwtAuthenticationToken.class, JwtHandshakeInterceptor.class, JwtSecurityFilter.class,
                                JwtUtil.class, SecurityConfig.class, WebSocketConfig.class
                        }
        )
)
// 엔티티 빈등록
@EntityScan("com.example.lastproject.domain.item")
// 레파지토리 빈등록
@EnableJpaRepositories(basePackages = "com.example.lastproject.domain.item.repository")
@EnableScheduling
@SpringBootApplication
public class ModuleBatchApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application");
        SpringApplication.run(ModuleBatchApplication.class, args);
    }

}

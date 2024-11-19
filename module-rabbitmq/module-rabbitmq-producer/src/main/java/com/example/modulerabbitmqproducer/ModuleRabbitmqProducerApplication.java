package com.example.modulerabbitmqproducer;

import com.example.lastproject.config.*;
import com.example.lastproject.domain.market.entity.Market;
import com.example.lastproject.domain.party.entity.Party;
import com.example.lastproject.domain.party.repository.PartyQueryRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.example.lastproject.domain.item.repository", "com.example.lastproject.domain.market.repository",
        "com.example.lastproject.domain.party.repository", "com.example.lastproject.domain.user.repository"})
@ComponentScan(
        basePackages = {"com.example.modulerabbitmqproducer"},
        basePackageClasses = {QueryDslConfig.class, Market.class, Party.class, PartyQueryRepositoryImpl.class}
        ,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes =
                        {       // 인증관련 빈등록 필터링
                                JwtAuthenticationToken.class, JwtHandshakeInterceptor.class, JwtSecurityFilter.class,
                                JwtUtil.class, SecurityConfig.class, WebSocketConfig.class
                        }
        )
)
@EntityScan(basePackages = {"com.example.lastproject.domain"})
@SpringBootApplication
public class ModuleRabbitmqProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleRabbitmqProducerApplication.class, args);
    }

}


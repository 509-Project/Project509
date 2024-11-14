package com.example.lastproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    /*
    Spring Security 의 보안 설정을 구성하는 클래스
    JWT 기반 인증을 구현하고, 각종 요청에 대한 권한 설정 수행
    BCrypt 해시 알고리즘을 사용하여 비밀번호를 암호화
     */

    private final JwtSecurityFilter jwtSecurityFilter;

    /**
     * BCryptPasswordEncoder bean 생성
     *
     * @return PasswordEncoder BCrypt 알고리즘을 사용한 비밀번호 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP 보안 설정 구성
     *
     * @param http HttpSecurity 객체를 사용하여 보안 설정을 정의
     * @return SecurityFilterChain 보안 필터 체인
     * @throws Exception 보안 설정 중 발생할 수 있는 예외 ( 최상위 예외 )
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        /*
        securityFilterChain 에서 비활성화된 기능 -> 서버의 상태 관리 x, 클라이언트에서 모든 인증 정보를 처리하도록 설계
        CSRF 보호 비활성화 - JWT 방식은 CSRF 공격에 대한 위험이 상대적으로 낮음
        상태 비저장 세션 정책 설정 - JWT 인증 방식은 세션을 사용 x, 즉 서버에서 사용자의 상태 저장 필요 x
         */

        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // SessionManagementFilter, SecurityContextPersistenceFilter
                )
                .addFilterBefore(jwtSecurityFilter, SecurityContextHolderAwareRequestFilter.class)
                .formLogin(AbstractHttpConfigurer::disable) // UsernamePasswordAuthenticationFilter, DefaultLoginPageGeneratingFilter 비활성화
                .anonymous(AbstractHttpConfigurer::disable) // AnonymousAuthenticationFilter 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // BasicAuthenticationFilter 비활성화
                .logout(AbstractHttpConfigurer::disable) // LogoutFilter 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signin", "/auth/signup", "/error","/markets/addresses").permitAll() // error -> 토큰이 없는 상태로 /error 경로로 리다이렉션 -> 403 error 발생
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // frontend 관련(css,js) 경로 permit
                        .requestMatchers("/ws/**", "/topic/**", "/app/**", "index.html", "/", "/chat/**").permitAll() // chat 관련 경로 및 frontend 관련(html) 경로 permit
                        .anyRequest().authenticated()
                )
                .build();
    }

}

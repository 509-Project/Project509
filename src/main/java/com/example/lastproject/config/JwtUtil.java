package com.example.lastproject.config;

import com.example.lastproject.common.CustomException;
import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    /*
    JWT( JSON Web Token )를 생성, 파싱 및 검증하는 기능을 제공하는 유틸리티
    JWT 를 안전하게 생성하고, 이를 사용하여 클라이언트와 서버 간의 인증을 처리
     */

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    /**
     * 클래스 초기화 메서드
     * secretKey 를 Base64로 디코딩하여 HMAC 서명에 사용할 키를 초기화
     * JWT 생성 및 검증을 위한 키 설정을 수행
     */
    @PostConstruct // PostConstruct 에너테이션 -
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * JWT 를 생성하는 메서드
     * 주어진 사용자 정보를 기반으로 JWT 를 생성
     * 토큰은 사용자의 ID를 주체로 설정하고, 이메일 및 역할을 클레임으로 추가
     * 토큰의 유효 기간 ( 60분 ), 발급일 포함
     *
     * @param userId   사용자 ID
     * @param email    사용자 이메일
     * @param userRole 사용자 권한 ( ROLE_USER, ROLE_ADMIN )
     * @return 생성된 JWT 문자열 (Bearer 접두사 포함)
     */
    public String createToken(Long userId, String email, UserRole userRole) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("email", email)
                        .claim("userRole", userRole)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    /**
     * Authorization 헤더에서 JWT 를 추출하는 메서드
     * 이 메서드는 Authorization 헤더에서 Bearer 접두사를 제거하고 JWT 를 반환
     * Bearer 접두사가 없거나 값이 비어있으면 CustomException 이 발생
     *
     * @param tokenValue Authorization 헤더의 값
     * @return 추출된 JWT 문자열
     * @throws CustomException TOKEN_NOT_FOUND 예외
     */
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
    }

    /**
     * 주어진 JWT 에서 클레임을 추출하는 메서드
     * 입력된 JWT 를 파싱하고 서명 검증을 수행하여 유효성을 확인
     * 유효한 JWT 일 경우 해당 JWT 에 포함된 클레임 정보를 반환
     *
     * @param token JWT 문자열 -> Bearer 접두사를 제외한 순수 JWT 값
     * @return JWT 의 클레임 정보를 담고 있는 {@link Claims} 객체
     * @throws io.jsonwebtoken.JwtException JWT 파싱 또는 서명 검증 실패 시 발생하는 예외
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

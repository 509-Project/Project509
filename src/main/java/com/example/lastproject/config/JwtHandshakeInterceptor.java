package com.example.lastproject.config;

import com.example.lastproject.domain.auth.entity.AuthUser;
import com.example.lastproject.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 채팅창 접속시, 인증된 사용자만 채팅을 사용할수 있도록 JWT검증을 수행하는 메서드
     * 모든 HTTP 요청에서 JWT를 통해 인증 절차를 거치도록 무상태 서비스 구성을 해두었기 때문에, WebSocket 요청에서도 동일하게 JWT 인증을 적용하는 것이 일관된 보안 체계를 유지
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // URL 파라미터에서 JWT 토큰 추출
        String token = request.getURI().getQuery();
        String jwt = null;

        // 파라미터가 "token=..." 형식인지 확인하여 JWT 값 추출
        if (token != null && token.startsWith("token=")) {
            jwt = token.substring("token=".length());
        }

        // JWT인증
        if (jwt != null) {
            try {

                Claims claims = jwtUtil.extractClaims(jwt);
                Long userId = Long.valueOf(claims.getSubject());
                String email = claims.get("email", String.class);
                UserRole userRole = UserRole.of(claims.get("userRole", String.class));

                // AuthUser 생성 후 WebSocket 세션에 저장
                AuthUser authUser = new AuthUser(userId, email, userRole);
                attributes.put("authUser", authUser);

                return true;

            } catch (SecurityException | MalformedJwtException | ExpiredJwtException e) {
                response.setStatusCode(HttpStatusCode.valueOf(401));
            } catch (UnsupportedJwtException e) {
                response.setStatusCode(HttpStatusCode.valueOf(400));
            } catch (Exception e) {
                response.setStatusCode(HttpStatusCode.valueOf(500));
            }
        }
        return false; // JWT 토큰이 없거나 잘못된 경우 연결 거부
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

}

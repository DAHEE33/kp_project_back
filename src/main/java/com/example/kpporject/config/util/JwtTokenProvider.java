package com.example.kpporject.config.util;
import com.example.kpporject.auth.userInfo.GoogleUserInfo;
import com.example.kpporject.auth.userInfo.KakaoUserInfo;
import com.example.kpporject.auth.userInfo.OAuth2UserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 실제 운영에서는 안전하게 보관된 비밀키를 사용하세요.
    private final Key key = Keys.hmacShaKeyFor("very-strong-secret-key-very-strong-secret-key".getBytes());

    // 토큰 유효기간 (예: 1일 = 86,400,000 밀리초, 1시간 = 3,600,000 밀리초)
    private final long JWT_EXPIRATION_MS = 3_600_000;

    /**
     * Authentication 객체를 기반으로 JWT 토큰을 생성합니다.
     * OAuth2 인증과 일반 인증 모두를 처리할 수 있도록 분기 처리합니다.
     */
    public String generateToken(Authentication authentication,String registrationId) {
        String username;
        Object principal = authentication.getPrincipal();

        // 일반 로그인 시 UserDetails 인스턴스인 경우
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        }
        // OAuth2 로그인 시 DefaultOAuth2User 인스턴스인 경우
        else if (principal instanceof DefaultOAuth2User oauthUser) {
            OAuth2UserInfo oAuth2UserInfo = null;
            // 구글의 경우 "email"이나 "sub" 등의 속성을 사용할 수 있습니다.
            if ("google".equals(registrationId)) {
                oAuth2UserInfo = new GoogleUserInfo(oauthUser.getAttributes());
            } else { // kko
                oAuth2UserInfo = new KakaoUserInfo(oauthUser.getAttributes());
            }
            username = oAuth2UserInfo.getEmail();
            System.out.println("JwtTokenProviderusername = " + username);
        }
        else {
            throw new IllegalArgumentException("Unknown principal type: " + principal.getClass());
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(username)  // JWT의 subject로 username 설정
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 subject(username)을 추출합니다.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // 필요 시 로깅 처리
        }
        return false;
    }
}
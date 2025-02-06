package com.example.kpporject.config.security;

import com.example.kpporject.config.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler { //Oauth2.0 로그인 성공 후처리 컴포넌트
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String token = tokenProvider.generateToken(authentication);

        // JWT를 담은 쿠키 생성
        Cookie jwtCookie = new Cookie("loginJwtToken", token);
        jwtCookie.setHttpOnly(false); // true로 설정하면 JS에서 접근 불가하므로, 프론트에서 직접 토큰 사용 시 false로 설정
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60); // 1시간 (초 단위)
        jwtCookie.setSecure(false); // 개발환경: false, 운영환경(HTTPS): true

        // 응답에 쿠키 추가
        response.addCookie(jwtCookie);

        // Vue.js 프론트엔드로 JWT 반환
        response.sendRedirect("http://localhost:8080");
    }
}

package com.example.kpporject.config.security;

import com.example.kpporject.service.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SecurityConfig.class);
    private final OAuth2LoginSuccessHandler successHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(OAuth2LoginSuccessHandler successHandler,
                          CustomOAuth2UserService customOAuth2UserService) {
        this.successHandler = successHandler;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    /**
     * SecurityFilterChain 빈을 정의하여 HTTP 보안 설정을 구성합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 및 CSRF 설정 (필요에 따라 조정)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                // URL 접근 권한 설정: 로그인 관련 엔드포인트는 모두 허용, 나머지는 인증 필요
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("pass/**", // 개발용 security filter 통과 uri
                                "/oauth2/authorization/google",
                                "/oauth2/authorization/kakao").permitAll()
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(successHandler) // 로그인 성공 시 JWT 발급 및 전달
                );
        // 모든 요청 전에 JWT 필터를 실행하여 토큰 검증 수행
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}

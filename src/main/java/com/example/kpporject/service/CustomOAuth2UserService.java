package com.example.kpporject.service;

import com.example.kpporject.auth.userInfo.GoogleUserInfo;
import com.example.kpporject.auth.userInfo.KakaoUserInfo;
import com.example.kpporject.auth.userInfo.OAuth2UserInfo;
import com.example.kpporject.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService { // Oauth2 로그인 시 루트

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserService userService;

    /**
     * OAuth2 공급자(예: Google)에서 사용자 정보를 로드한 후,
     * 필요 시 추가 가공(예: DB 저장, 속성 매핑 등)을 진행할 수 있습니다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 현재 OAuth 공급자 확인 (예: "google")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth Provider: {}", registrationId);

        OAuth2UserInfo oAuth2UserInfo = null;

        // OAuth2User에서 사용자 속성 추출
        String email ;
        String sub ;
        String name ;
        if (registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }
        else { //kko
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }
        email = oAuth2UserInfo.getEmail();
        sub = oAuth2UserInfo.getProviderId();
        name = oAuth2UserInfo.getName();

        Optional<User> optionalUser = userService.getUserByEmail(email);

        if (optionalUser.isEmpty()) {
            // 신규 사용자 생성
            log.info("New user. Creating a new user record.");
            User saveUser = User.builder()
                    .email(email)
                    .oauthProvider(registrationId)
                    .createdAt(LocalDateTime.now())
                    .oauthId(sub)
                    .username(name)
                    .build();

            userService.saveUser(saveUser);
        } else {
            log.info("exist user. loginNow");
        }
        return oAuth2User;
    }
}
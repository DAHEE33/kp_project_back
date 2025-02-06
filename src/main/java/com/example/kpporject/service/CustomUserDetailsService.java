package com.example.kpporject.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * 실제 운영에서는 DB 등에서 사용자를 조회하여 UserDetails를 구성합니다.
     * 예제에서는 단순히 ROLE_USER 권한만 부여한 사용자 객체를 반환합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return User.withUsername(username)
                .password("") // JWT 인증에서는 패스워드를 사용하지 않으므로 빈 문자열
                .authorities("ROLE_USER")
                .build();
    }
}
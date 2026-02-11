package com.seoulorigin.OJK.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // [1] 암호화 모듈 빈 등록 (기존 코드)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // [2] 보안 필터 체인 설정 (새로 추가된 부분)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API이므로 CSRF 보안 비활성화 (토큰 방식이라 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // 기본 로그인 창(Form Login)과 HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 세션을 사용하지 않음 (JWT를 쓸 것이므로 STATELESS로 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청 주소별 권한 설정
                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/", "/index.html", "/static/**").permitAll()
//                        // "/api/auth/**" 로 시작하는 모든 요청(로그인, 가입, 이메일)은 누구나 접근 가능
//                        .requestMatchers("/api/auth/**", "/error").permitAll()
//                        .requestMatchers("/api/member/signup").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/member/**").permitAll()
//                        // 그 외의 모든 요청은 인증된 회원만 접근 가능
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                );

        return http.build();
    }
}
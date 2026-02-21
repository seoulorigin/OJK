package com.seoulorigin.OJK.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final SessionAuthenticationFilter sessionAuthenticationFilter;

    public SecurityConfig(SessionAuthenticationFilter sessionAuthenticationFilter) {
        this.sessionAuthenticationFilter = sessionAuthenticationFilter;
    }

    // [1] 암호화 모듈 빈 등록 (기존 코드)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // [2] 보안 필터 체인 설정 (새로 추가된 부분)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 초기 개발 편의를 위해 CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 기본 로그인 창(Form Login)과 HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 세션 기반 인증 사용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 요청 주소별 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 정적 리소스
                        .requestMatchers("/", "/index.html", "/static/**", "/error").permitAll()

                        // 인증(공개): 회원가입/이메일 인증/로그인
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/email/send",
                                "/api/auth/email/verify",
                                "/api/auth/login",
                                "/api/auth/signup",
                                "/api/member/signup"
                        ).permitAll()

                        // 멤버 조회(공개)
                        .requestMatchers(HttpMethod.GET,
                                "/api/member/{name}",
                                "/api/member/path",
                                "/api/member/{id}/followers"
                        ).permitAll()

                        // 세션 필요: 현재 사용자 기준 API
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/member/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/member/me/**").authenticated()

                        // 세션 필요: 멤버 관련 변경 작업
                        .requestMatchers(HttpMethod.POST, "/api/member/**").authenticated()

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

package com.seoulorigin.OJK.domain.auth.service;

import com.seoulorigin.OJK.domain.auth.dto.LoginRequest;
import com.seoulorigin.OJK.domain.auth.dto.TokenResponse;
import com.seoulorigin.OJK.domain.auth.jwt.JwtTokenProvider;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.PasswordAuthentication;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email());
        if (!passwordEncoder.matches(request.password(), member.getPassword()))
            throw new IllegalArgumentException("Wrong Password.");
        String token = jwtTokenProvider.createToken(member.getId(), member.getEmail());
        return TokenResponse.of(token);
    }
}

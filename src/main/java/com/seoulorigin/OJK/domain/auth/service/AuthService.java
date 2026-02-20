package com.seoulorigin.OJK.domain.auth.service;

import com.seoulorigin.OJK.domain.auth.dto.LoginRequest;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    public static final String SESSION_MEMBER_ID = "auth:memberId";
    public static final String SESSION_MEMBER_EMAIL = "auth:memberEmail";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void login(LoginRequest request, HttpSession session) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일."));

        if (!passwordEncoder.matches(request.password(), member.getPassword()))
            throw new IllegalArgumentException("Wrong Password.");

        session.setAttribute(SESSION_MEMBER_ID, member.getId());
        session.setAttribute(SESSION_MEMBER_EMAIL, member.getEmail());
        session.setMaxInactiveInterval(60 * 60);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public Long getCurrentMemberId(HttpSession session) {
        Object memberId = session.getAttribute(SESSION_MEMBER_ID);
        if (memberId instanceof Long id) {
            return id;
        }
        throw new AccessDeniedException("로그인이 필요합니다.");
    }
}

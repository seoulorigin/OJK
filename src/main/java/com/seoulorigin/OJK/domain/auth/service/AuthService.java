package com.seoulorigin.OJK.domain.auth.service;

import com.seoulorigin.OJK.domain.auth.dto.LoginRequest;
import com.seoulorigin.OJK.domain.member.dto.MemberSignupRequest;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import com.seoulorigin.OJK.domain.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.seoulorigin.OJK.global.exception.BusinessException;
import com.seoulorigin.OJK.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    public static final String SESSION_MEMBER_ID = "auth:memberId";
    public static final String SESSION_MEMBER_EMAIL = "auth:memberEmail";

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Member signup(MemberSignupRequest request) {
        return memberService.signup(request);
    }

    @Transactional
    public void login(LoginRequest request, HttpSession session) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), member.getPassword()))
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);

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
        throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }
}

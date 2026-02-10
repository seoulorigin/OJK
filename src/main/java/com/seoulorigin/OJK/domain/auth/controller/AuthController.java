package com.seoulorigin.OJK.domain.auth.controller;

import com.seoulorigin.OJK.domain.auth.dto.EmailRequest;
import com.seoulorigin.OJK.domain.auth.dto.EmailVerificationRequest;
import com.seoulorigin.OJK.domain.auth.dto.LoginRequest;
import com.seoulorigin.OJK.domain.auth.dto.TokenResponse;
import com.seoulorigin.OJK.domain.auth.service.AuthService;
import com.seoulorigin.OJK.domain.auth.service.EmailService;
import com.seoulorigin.OJK.domain.auth.repository.VerificationStore;
import com.seoulorigin.OJK.domain.member.dto.MemberResponse;
import com.seoulorigin.OJK.domain.member.dto.MemberSignupRequest;
import com.seoulorigin.OJK.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final EmailService emailService;
    private final VerificationStore verificationStore;
    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/email/send")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid EmailRequest request) {
        if (!request.email().endsWith("@dankook.ac.kr")) {
            return ResponseEntity.badRequest().body("학교 웹 메일 아님.");
        }
        String code = emailService.sendMail(request.email());
        verificationStore.saveCode(request.email(), code);
        return ResponseEntity.ok("인증 번호 발송완료.");
    }

    @PostMapping("/email/verify")
    public ResponseEntity<String> verifyEmail(@RequestBody @Valid EmailVerificationRequest request) {
        // [테스트용]
        if ("000000".equals(request.code())) {
            verificationStore.verify(request.email());
            return ResponseEntity.ok("개발자 프리패스 인증완료.");
        }

        boolean verified = verificationStore.verifyCode(request.email(), request.code());
        if (verified) {
            return ResponseEntity.ok("인증완료.");
        } else {
            return ResponseEntity.badRequest().body("불일치 혹은 만료");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@RequestBody @Valid MemberSignupRequest request) {
        return ResponseEntity.ok(MemberResponse.from(memberService.signup(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

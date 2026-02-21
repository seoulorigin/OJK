package com.seoulorigin.OJK.domain.member.service;

import com.seoulorigin.OJK.domain.auth.repository.VerificationStore;
import com.seoulorigin.OJK.domain.major.entity.Major;
import com.seoulorigin.OJK.domain.major.repository.MajorRepository;
import com.seoulorigin.OJK.domain.member.dto.MemberSignupRequest;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import com.seoulorigin.OJK.global.exception.BusinessException;
import com.seoulorigin.OJK.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private VerificationStore verificationStore;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MajorRepository majorRepository;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository, verificationStore, passwordEncoder, majorRepository);
    }

    @Test
    void signup_이메일인증이안되면예외() {
        MemberSignupRequest request = new MemberSignupRequest(
                "test@seoul.ac.kr", "Aa123456!", "tester", 23, "Engineering", "CS", "insta", "bio");

        when(verificationStore.isVerified(request.email())).thenReturn(false);

        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_NOT_VERIFIED);
    }

    @Test
    void signup_비밀번호정책위반시예외() {
        MemberSignupRequest request = new MemberSignupRequest(
                "test@seoul.ac.kr", "weak", "tester", 23, "Engineering", "CS", "insta", "bio");

        when(verificationStore.isVerified(request.email())).thenReturn(true);

        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PASSWORD_POLICY);
    }

    @Test
    void signup_정상요청이면회원을저장하고인증정보를삭제한다() {
        MemberSignupRequest request = new MemberSignupRequest(
                "test@seoul.ac.kr", "Aa123456!", "tester", 23, "Engineering", "CS", "insta", "bio");

        Major major = new Major("CS", "Engineering");

        when(verificationStore.isVerified(request.email())).thenReturn(true);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(majorRepository.findByMajorName("CS")).thenReturn(Optional.of(major));
        when(memberRepository.save(org.mockito.ArgumentMatchers.any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Member saved = memberService.signup(request);

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());
        verify(verificationStore).remove(request.email());

        Member captured = memberCaptor.getValue();
        assertThat(captured.getEmail()).isEqualTo("test@seoul.ac.kr");
        assertThat(captured.getPassword()).isEqualTo("encoded-password");
        assertThat(captured.getMajor()).isEqualTo(major);

        assertThat(saved.getName()).isEqualTo("tester");
    }
}

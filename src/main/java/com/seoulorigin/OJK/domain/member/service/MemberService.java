package com.seoulorigin.OJK.domain.member.service;

import com.seoulorigin.OJK.domain.auth.repository.VerificationStore;
import com.seoulorigin.OJK.domain.major.entity.Major;
import com.seoulorigin.OJK.domain.major.repository.MajorRepository;
import com.seoulorigin.OJK.domain.member.dto.MemberSignupRequest;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final VerificationStore verificationStore;
    private final PasswordEncoder passwordEncoder;
    private final MajorRepository majorRepository;

    @Transactional
    public Member signup(MemberSignupRequest request) {
        if (!verificationStore.isVerified(request.email()))
            throw  new IllegalArgumentException("선인증 필수");


        Member member = new Member();
        member.setEmail(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        member.setPassword(encodedPassword);

        member.setName(request.name());
        member.setAdmissionYear(request.admissionYear());
        member.setInstagramId(request.instagramId());
        member.setBio(request.bio());

        Major major = majorRepository.findByMajorName(request.majorName())
                        .orElseGet(() -> new Major(request.majorName(), request.college()));
        member.setMajor(major);

        verificationStore.remove(request.email());

        return memberRepository.save(member);
    }

    @Transactional
    public List<Member> search(String name, Integer admissionYear, String majorName) {
        return memberRepository.searchMembers(name, admissionYear, majorName);
    }

    @Transactional
    public List<Member> getPath(Long startId, Long endId) {
        List<Member> path = memberRepository.findPathById(startId, endId);
        if (path.isEmpty()) {
            throw new IllegalArgumentException("No Path!");
        }
        return path;
    }
}

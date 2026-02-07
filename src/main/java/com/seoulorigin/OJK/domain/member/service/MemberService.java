package com.seoulorigin.OJK.domain.member.service;

import com.seoulorigin.OJK.domain.major.Major;
import com.seoulorigin.OJK.domain.member.dto.MemberSignupRequest;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member signup(MemberSignupRequest request) {
        Member member = new Member();
        member.setEmail(request.email());
        member.setPassword(request.password()); // 실제로는 암호화가 필요합니다.
        member.setName(request.name());
        member.setAdmissionYear(request.admissionYear());
        member.setInstagramId(request.instagramId());
        member.setBio(request.bio());

        Major major = new Major(request.majorName(), request.college());
        member.setMajor(major);

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

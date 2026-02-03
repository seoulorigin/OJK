package com.seoulorigin.OJK.domain.member.service;

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
    public List<Member> getPath(Long startId, Long endId) {
        List<Member> path = memberRepository.findPathById(startId, endId);
        if (path.isEmpty()) {
            throw new IllegalArgumentException("No Path!");
        }
        return path;
    }
}

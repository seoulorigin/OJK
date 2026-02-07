package com.seoulorigin.OJK.domain.follow.service;

import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final MemberRepository memberRepository;

    @Transactional
    public void follow(Long fromId, Long toId) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        Member fromMember = memberRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID=" + fromId));

        Member toMember = memberRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다. ID=" + toId));

        // fromMember가 toMember를 팔로우 (관계 생성)
        fromMember.follow(toMember);

        // 변경 사항 저장 (Neo4j가 자동으로 관계를 연결해줍니다)
        memberRepository.save(fromMember);
    }

    @Transactional
    public List<Member> getFollowers(Long id) {
        return memberRepository.findFollowersById(id);
    }
}

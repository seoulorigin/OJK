package com.seoulorigin.OJK.domain.follow.service;

import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import com.seoulorigin.OJK.global.exception.BusinessException;
import com.seoulorigin.OJK.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final MemberRepository memberRepository;

    @Transactional
    public void requestFollow(Long actorId, Long fromId, Long toId) {
        validateActorOwnsAccount(actorId, fromId);
        validateFollowTarget(fromId, toId);
        validateMemberExists(fromId);
        validateMemberExists(toId);

        if (memberRepository.existsFollowRelation(fromId, toId)) {
            throw new BusinessException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }
        if (memberRepository.existsFollowRequest(fromId, toId)) {
            throw new BusinessException(ErrorCode.FOLLOW_REQUEST_ALREADY_EXISTS);
        }

        memberRepository.createFollowRequest(fromId, toId);
    }

    @Transactional
    public void approveFollowRequest(Long actorId, Long requesterId, Long targetId) {
        validateActorOwnsAccount(actorId, targetId);
        long affected = memberRepository.approveFollowRequest(requesterId, targetId);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.FOLLOW_REQUEST_NOT_FOUND);
        }
    }

    @Transactional
    public void rejectFollowRequest(Long actorId, Long requesterId, Long targetId) {
        validateActorOwnsAccount(actorId, targetId);
        long affected = memberRepository.rejectFollowRequest(requesterId, targetId);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.FOLLOW_REQUEST_NOT_FOUND);
        }
    }

    @Transactional
    public void unfollow(Long actorId, Long fromId, Long toId) {
        validateActorOwnsAccount(actorId, fromId);
        validateFollowTarget(fromId, toId);

        long affected = memberRepository.unfollow(fromId, toId);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.FOLLOW_RELATION_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<Member> getFollowers(Long id) {
        return memberRepository.findFollowersById(id);
    }

    @Transactional(readOnly = true)
    public List<Member> getPendingRequests(Long id) {
        return memberRepository.findPendingFollowRequestsByMemberId(id);
    }

    private void validateActorOwnsAccount(Long actorId, Long accountId) {
        if (!actorId.equals(accountId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인 계정으로만 수행할 수 있습니다.");
        }
    }

    private void validateFollowTarget(Long fromId, Long toId) {
        if (fromId.equals(toId)) {
            throw new BusinessException(ErrorCode.FOLLOW_SELF_NOT_ALLOWED);
        }
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "사용자를 찾을 수 없습니다. ID=" + memberId);
        }
    }
}

package com.seoulorigin.OJK.domain.follow.service;

import com.seoulorigin.OJK.domain.member.repository.MemberRepository;
import com.seoulorigin.OJK.global.exception.BusinessException;
import com.seoulorigin.OJK.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private FollowService followService;

    @BeforeEach
    void setUp() {
        followService = new FollowService(memberRepository);
    }

    @Test
    void requestFollow_성공시요청을생성한다() {
        Long actorId = 1L;
        Long fromId = 1L;
        Long toId = 2L;

        when(memberRepository.existsById(fromId)).thenReturn(true);
        when(memberRepository.existsById(toId)).thenReturn(true);
        when(memberRepository.existsFollowRelation(fromId, toId)).thenReturn(false);
        when(memberRepository.existsFollowRequest(fromId, toId)).thenReturn(false);

        followService.requestFollow(actorId, fromId, toId);

        verify(memberRepository).createFollowRequest(fromId, toId);
    }

    @Test
    void requestFollow_본인계정이아니면예외() {
        assertThatThrownBy(() -> followService.requestFollow(9L, 1L, 2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);

        verify(memberRepository, never()).createFollowRequest(1L, 2L);
    }

    @Test
    void requestFollow_이미팔로우중이면예외() {
        Long actorId = 1L;
        Long fromId = 1L;
        Long toId = 2L;

        when(memberRepository.existsById(fromId)).thenReturn(true);
        when(memberRepository.existsById(toId)).thenReturn(true);
        when(memberRepository.existsFollowRelation(fromId, toId)).thenReturn(true);

        assertThatThrownBy(() -> followService.requestFollow(actorId, fromId, toId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FOLLOW_ALREADY_EXISTS);
    }

    @Test
    void approveFollowRequest_대상요청이없으면예외() {
        when(memberRepository.approveFollowRequest(2L, 1L)).thenReturn(0L);

        assertThatThrownBy(() -> followService.approveFollowRequest(1L, 2L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FOLLOW_REQUEST_NOT_FOUND);
    }

    @Test
    void unfollow_성공하면관계를삭제한다() {
        when(memberRepository.unfollow(1L, 2L)).thenReturn(1L);

        followService.unfollow(1L, 1L, 2L);

        verify(memberRepository).unfollow(1L, 2L);
    }

    @Test
    void unfollow_관계가없으면예외() {
        when(memberRepository.unfollow(1L, 2L)).thenReturn(0L);

        assertThatThrownBy(() -> followService.unfollow(1L, 1L, 2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FOLLOW_RELATION_NOT_FOUND);
    }

    @Test
    void requestFollow_존재하지않는회원이면예외메시지에ID포함() {
        when(memberRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> followService.requestFollow(1L, 1L, 2L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
                    assertThat(businessException.getMessage()).contains("ID=2");
                });
    }
}

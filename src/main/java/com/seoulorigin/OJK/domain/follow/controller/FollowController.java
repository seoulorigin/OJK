package com.seoulorigin.OJK.domain.follow.controller;

import com.seoulorigin.OJK.domain.auth.service.AuthService;
import com.seoulorigin.OJK.domain.follow.service.FollowService;
import com.seoulorigin.OJK.domain.member.dto.MemberResponse;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;
    private final AuthService authService;

    @PostMapping("/{fromId}/follow/{toId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @PathVariable Long fromId,
            @PathVariable Long toId,
            HttpSession session
    ) {
        Long actorId = authService.getCurrentMemberId(session);
        followService.requestFollow(actorId, fromId, toId);
        return ResponseEntity.ok(ApiResponse.success("팔로우 요청이 전송되었습니다."));
    }

    @PostMapping("/me/follow/{toId}")
    public ResponseEntity<ApiResponse<Void>> followMe(
            @PathVariable Long toId,
            HttpSession session
    ) {
        Long actorId = authService.getCurrentMemberId(session);
        followService.requestFollow(actorId, actorId, toId);
        return ResponseEntity.ok(ApiResponse.success("팔로우 요청이 전송되었습니다."));
    }

    @DeleteMapping("/{fromId}/follow/{toId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long fromId,
            @PathVariable Long toId,
            HttpSession session
    ) {
        Long actorId = authService.getCurrentMemberId(session);
        followService.unfollow(actorId, fromId, toId);
        return ResponseEntity.ok(ApiResponse.success("언팔로우가 완료되었습니다."));
    }

    @DeleteMapping("/me/follow/{toId}")
    public ResponseEntity<ApiResponse<Void>> unfollowMe(
            @PathVariable Long toId,
            HttpSession session
    ) {
        Long actorId = authService.getCurrentMemberId(session);
        followService.unfollow(actorId, actorId, toId);
        return ResponseEntity.ok(ApiResponse.success("언팔로우가 완료되었습니다."));
    }

    @PostMapping("/me/follow-requests/{fromId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveRequest(
            @PathVariable Long fromId,
            HttpSession session
    ) {
        Long actorId = authService.getCurrentMemberId(session);
        followService.approveFollowRequest(actorId, fromId, actorId);
        return ResponseEntity.ok(ApiResponse.success("팔로우 요청을 승인했습니다."));
    }

    @PostMapping("/me/follow-requests/{fromId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectRequest(
            @PathVariable Long fromId,
            HttpSession session
    ) {
        Long actorId = authService.getCurrentMemberId(session);
        followService.rejectFollowRequest(actorId, fromId, actorId);
        return ResponseEntity.ok(ApiResponse.success("팔로우 요청을 거절했습니다."));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getFollowerList(@PathVariable Long id) {
        List<Member> members = followService.getFollowers(id);
        List<MemberResponse> responses = members.stream().map(MemberResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success("팔로워 목록 조회 성공", responses));
    }

    @GetMapping("/me/follow-requests")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getMyPendingRequests(HttpSession session) {
        Long actorId = authService.getCurrentMemberId(session);
        List<MemberResponse> responses = followService.getPendingRequests(actorId)
                .stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("팔로우 요청 목록 조회 성공", responses));
    }
}

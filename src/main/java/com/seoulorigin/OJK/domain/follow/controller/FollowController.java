package com.seoulorigin.OJK.domain.follow.controller;

import com.seoulorigin.OJK.domain.auth.service.AuthService;
import com.seoulorigin.OJK.domain.follow.service.FollowService;
import com.seoulorigin.OJK.domain.member.dto.MemberResponse;
import com.seoulorigin.OJK.global.response.ApiResponse;
import com.seoulorigin.OJK.domain.member.entity.Member;
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
        followService.follow(actorId, fromId, toId);
        return ResponseEntity.ok(ApiResponse.success("팔로우가 완료되었습니다."));
    }

    @PostMapping("/me/follow/{toId}")
    public ResponseEntity<ApiResponse<Void>> followMe(
            @PathVariable Long toId,
            HttpSession session
    ) {
        Long actorId = authService.getCurrentMemberId(session);
        followService.follow(actorId, actorId, toId);
        return ResponseEntity.ok(ApiResponse.success("팔로우가 완료되었습니다."));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getFollowerList(@PathVariable Long id) {
        List<Member> members = followService.getFollowers(id);
        List<MemberResponse> responses = members.stream()
                .map(MemberResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.success("팔로워 목록 조회 성공", responses));
    }
}

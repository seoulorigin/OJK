package com.seoulorigin.OJK.domain.follow.controller;

import com.seoulorigin.OJK.domain.follow.service.FollowService;
import com.seoulorigin.OJK.domain.member.dto.MemberResponse;
import com.seoulorigin.OJK.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{fromId}/follow/{toId}")
    public ResponseEntity<String> follow(
            @PathVariable Long fromId,
            @PathVariable Long toId
    ) {
        followService.follow(fromId, toId);
        return ResponseEntity.ok("Success Follow.");
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<List<MemberResponse>> getFollowerList(@PathVariable Long id) {
        List<Member> members = followService.getFollowers(id);
        List<MemberResponse> responses = members.stream()
                .map(MemberResponse::from).toList();
        return ResponseEntity.ok(responses);
    }
}

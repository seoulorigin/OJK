package com.seoulorigin.OJK.domain.member.Controller;

import com.seoulorigin.OJK.domain.member.dto.MemberResponse;
import com.seoulorigin.OJK.domain.member.dto.MemberSignupRequest;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@RequestBody MemberSignupRequest request) {
        Member response = memberService.signup(request);
        return ResponseEntity.ok(MemberResponse.from(response));
    }

    @GetMapping("/path")
    public ResponseEntity<List<Member>> findPath(
            @RequestParam Long startId,
            @RequestParam Long endId
    ) {
        return ResponseEntity.ok(memberService.getPath(startId, endId));
    }
}

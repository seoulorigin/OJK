package com.seoulorigin.OJK.domain.member.controller;

import com.seoulorigin.OJK.domain.member.dto.MemberResponse;
import com.seoulorigin.OJK.domain.member.dto.MemberSignupRequest;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@RequestBody @Valid MemberSignupRequest request) {
        Member response = memberService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MemberResponse.from(response));
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<MemberResponse>> findMember(
            @PathVariable String name,
            @RequestParam(required = false) Integer admissionYear,
            @RequestParam(required = false) String majorName
    ) {
        List<Member> members = memberService.search(name, admissionYear, majorName);
        List<MemberResponse> responses = members.stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/path")
    public ResponseEntity<List<Member>> findPath(
            @RequestParam Long startId,
            @RequestParam Long endId
    ) {
        return ResponseEntity.ok(memberService.getPath(startId, endId));
    }


}

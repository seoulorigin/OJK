package com.seoulorigin.OJK.domain.member.controller;

import com.seoulorigin.OJK.domain.auth.service.AuthService;
import com.seoulorigin.OJK.domain.member.dto.MemberPathNodeResponse;
import com.seoulorigin.OJK.domain.member.dto.MemberResponse;
import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;


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
    public ResponseEntity<List<MemberPathNodeResponse>> findPath(
            @RequestParam Long startId,
            @RequestParam Long endId
    ) {
        List<MemberPathNodeResponse> responses = memberService.getPath(startId, endId).stream()
                .map(MemberPathNodeResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/me/path")
    public ResponseEntity<List<MemberPathNodeResponse>> findPathFromMe(
            @RequestParam Long endId,
            HttpSession session
    ) {
        List<MemberPathNodeResponse> responses = memberService
                .getPath(authService.getCurrentMemberId(session), endId)
                .stream()
                .map(MemberPathNodeResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }


}

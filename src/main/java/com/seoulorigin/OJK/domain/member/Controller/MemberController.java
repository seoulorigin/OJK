package com.seoulorigin.OJK.domain.member.Controller;

import com.seoulorigin.OJK.domain.member.entity.Member;
import com.seoulorigin.OJK.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/path")
    public RequestEntity<List<Member>> findPath(
            @RequestParam Long startId,
            @RequestParam Long endId
    ) {
        return ResponseEntity.ok(memberService.getPath(startId, endId));
    }
}

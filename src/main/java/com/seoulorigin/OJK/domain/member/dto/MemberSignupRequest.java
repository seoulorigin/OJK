package com.seoulorigin.OJK.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 회원가입 요청
 * @param email 학교 이메일
 * @param password 비밀번호
 * @param name 실명
 * @param admissionYear 학번(입학년도)
 * @param college 소속대학
 * @param majorName 전공명
 * @param instagramId 인스타그램 ID [선택]
 * @param bio 자기소개 [선택]
 */
public record MemberSignupRequest(
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotNull Integer admissionYear,
        @NotBlank String college,
        @NotBlank String majorName,
        String instagramId,
        String bio
) {

}

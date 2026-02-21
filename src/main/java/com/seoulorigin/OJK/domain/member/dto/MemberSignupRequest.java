package com.seoulorigin.OJK.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
        @NotBlank
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,64}$",
                message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        String password,
        @NotBlank String name,
        @NotNull Integer admissionYear,
        @NotBlank String college,
        @NotBlank String majorName,
        String instagramId,
        String bio
) {

}

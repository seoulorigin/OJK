package com.seoulorigin.OJK.domain.member.dto;

import com.seoulorigin.OJK.domain.member.entity.Member;

/**
 * 경로 탐색 응답 노드 DTO
 * 민감 정보(email, password 등)는 제외하고 최소 필드만 제공합니다.
 */
public record MemberPathNodeResponse(
        Long id,
        String name,
        Integer admissionYear,
        String majorName
) {
    public static MemberPathNodeResponse from(Member member) {
        return new MemberPathNodeResponse(
                member.getId(),
                member.getName(),
                member.getAdmissionYear(),
                member.getMajor() != null ? member.getMajor().getMajorName() : null
        );
    }
}

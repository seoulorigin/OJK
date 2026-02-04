package com.seoulorigin.OJK.domain.member.dto;

import com.seoulorigin.OJK.domain.member.entity.Member;

public record MemberResponse(
        Long id,
        String email,
        String name,
        int admissionYear,
        String college,
        String majorName,
        String instagramId,
        String bio
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getAdmissionYear(),
                member.getMajor().getCollege(),
                member.getMajor().getMajorName(),
                member.getInstagramId(),
                member.getBio()
        );
    }
}

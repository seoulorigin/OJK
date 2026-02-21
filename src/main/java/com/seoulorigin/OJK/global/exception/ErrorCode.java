package com.seoulorigin.OJK.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_400", "요청 값이 올바르지 않습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_401", "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_403", "접근 권한이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "AUTH_400_1", "이메일 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_400_1", "사용자를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH_400_2", "이메일 인증이 필요합니다."),
    INVALID_PASSWORD_POLICY(HttpStatus.BAD_REQUEST, "AUTH_400_3", "비밀번호 정책을 만족하지 않습니다."),
    FOLLOW_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FOLLOW_400_1", "자기 자신을 팔로우할 수 없습니다."),
    FOLLOW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FOLLOW_400_2", "이미 팔로우 중입니다."),
    FOLLOW_REQUEST_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FOLLOW_400_3", "이미 팔로우 요청을 보냈습니다."),
    FOLLOW_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOW_404_1", "팔로우 요청을 찾을 수 없습니다."),
    FOLLOW_RELATION_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOW_404_2", "팔로우 관계를 찾을 수 없습니다."),
    PATH_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER_400_2", "두 사용자 사이의 경로를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;
}

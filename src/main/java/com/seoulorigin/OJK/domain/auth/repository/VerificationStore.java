package com.seoulorigin.OJK.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

// TODO: 이메일 인증 토큰 및 인증 번호 저장 -> Redis

@Component // 현재는 유틸리티 성격 저장소
@RequiredArgsConstructor
public class VerificationStore {
    private final StringRedisTemplate redisTemplate;

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set("auth:code:" + email, code, Duration.ofMinutes(5));
        redisTemplate.delete("auth:status:" + email);
    }

    public boolean verifyCode(String email, String inputCode) {
        String key = "auth:code:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(inputCode)) {
            redisTemplate.delete(key);
            redisTemplate.opsForValue().set("auth:status:" + email, "true", Duration.ofHours(1));
            return true;
        }
        return false;
    }

    public boolean isVerified(String email) {
        String status = redisTemplate.opsForValue().get("auth:status:" + email);
        return "true".equals(status);
    }

    public void remove(String email) {
        redisTemplate.delete("auth:status:" + email);
    }

    // [테스트용]
    public void verify(String email) {
        redisTemplate.opsForValue().set("auth:status:" + email, "true", Duration.ofHours(1));
    }
}

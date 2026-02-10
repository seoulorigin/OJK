package com.seoulorigin.OJK.domain.auth.repository;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 이메일 인증 토큰 및 인증 번호 저장 -> Redis

@Component // 현재는 유틸리티 성격 저장소
public class VerificationStore {
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();
    private final Map<String, Boolean> statusStore = new ConcurrentHashMap<>();

    public void saveCode(String email, String code) {
        codeStore.put(email, code);
        statusStore.put(email, false);
    }

    public boolean verifyCode(String email, String inputCode) {
        if (!codeStore.containsKey(email)) return false;
        if (codeStore.get(email).equals(inputCode)) {
            codeStore.remove(email);
            statusStore.put(email, true);
            return true;
        }
        return false;
    }

    public boolean isVerified(String email) {
        return statusStore.getOrDefault(email, false);
    }

    public void remove(String email) {
        statusStore.remove(email);
    }
}

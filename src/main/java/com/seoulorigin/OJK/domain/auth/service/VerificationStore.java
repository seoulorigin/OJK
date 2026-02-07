package com.seoulorigin.OJK.domain.auth.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
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

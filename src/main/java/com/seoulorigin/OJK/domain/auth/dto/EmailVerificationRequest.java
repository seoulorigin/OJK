package com.seoulorigin.OJK.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(
        @NotBlank String email,
        @NotBlank String code
) {
}

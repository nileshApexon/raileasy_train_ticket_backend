package com.raileasy.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequestDto(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 72) String password,
        @NotBlank @Size(max = 120) String name
) {
}


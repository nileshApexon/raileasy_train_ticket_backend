package com.raileasy.dto.auth;

import java.util.UUID;

public record AuthResponseDto(
        UUID userId,
        String email,
        String name,
        boolean isAdmin,
        String token
) {
}


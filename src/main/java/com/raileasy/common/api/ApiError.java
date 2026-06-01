package com.raileasy.common.api;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        String path,
        String error,
        String message
) {
}


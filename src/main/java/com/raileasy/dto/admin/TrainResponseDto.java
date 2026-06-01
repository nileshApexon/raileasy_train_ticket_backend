package com.raileasy.dto.admin;

import java.util.UUID;

public record TrainResponseDto(
        UUID id,
        String trainNumber,
        String trainName,
        int seatsPerClass
) {
}


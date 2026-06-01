package com.raileasy.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrainRequestDto(
        @NotBlank @Size(max = 20) String trainNumber,
        @NotBlank @Size(max = 150) String trainName,
        @Min(1) int seatsPerClass
) {
}


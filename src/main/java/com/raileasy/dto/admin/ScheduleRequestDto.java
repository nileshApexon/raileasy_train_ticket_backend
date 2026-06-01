package com.raileasy.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleRequestDto(
        @NotNull UUID trainId,
        @NotBlank @Size(max = 120) String fromStation,
        @NotBlank @Size(max = 120) String toStation,
        @NotNull LocalDateTime departureTime,
        @NotNull LocalDateTime arrivalTime,
        @NotNull LocalDate journeyDate,
        @NotNull BigDecimal fareSleeper,
        @NotNull BigDecimal fareAc3,
        @NotNull BigDecimal fareAc2
) {
}


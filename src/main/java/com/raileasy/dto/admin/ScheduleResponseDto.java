package com.raileasy.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleResponseDto(
        UUID id,
        UUID trainId,
        String trainNumber,
        String trainName,
        String fromStation,
        String toStation,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        LocalDate journeyDate,
        BigDecimal fareSleeper,
        BigDecimal fareAc3,
        BigDecimal fareAc2
) {
}


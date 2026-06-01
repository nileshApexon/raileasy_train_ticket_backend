package com.raileasy.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ScheduleSearchResponseDto(
        UUID scheduleId,
        String trainNumber,
        String trainName,
        String fromStation,
        String toStation,
        LocalDate journeyDate,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        List<ClassAvailabilityDto> classAvailability
) {
}


package com.raileasy.dto.booking;

import com.raileasy.domain.BookingStatus;
import com.raileasy.domain.TravelClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingResponseDto(
        UUID bookingId,
        String pnrNumber,
        BookingStatus status,
        TravelClass travelClass,
        List<String> seatNumbers,
        UUID scheduleId,
        String trainNumber,
        String trainName,
        String fromStation,
        String toStation,
        LocalDate journeyDate,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
) {
}


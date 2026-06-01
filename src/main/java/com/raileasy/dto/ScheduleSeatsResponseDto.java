package com.raileasy.dto;

import com.raileasy.domain.TravelClass;

import java.util.List;
import java.util.UUID;

public record ScheduleSeatsResponseDto(
		UUID scheduleId,
		TravelClass travelClass,
		int totalSeats,
		List<String> bookedSeatNumbers
) {
}


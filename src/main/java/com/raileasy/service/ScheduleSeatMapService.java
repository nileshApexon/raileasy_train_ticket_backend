package com.raileasy.service;

import com.raileasy.domain.Booking;
import com.raileasy.domain.BookingStatus;
import com.raileasy.domain.Schedule;
import com.raileasy.domain.TravelClass;
import com.raileasy.dto.ScheduleSeatsResponseDto;
import com.raileasy.repository.BookingRepository;
import com.raileasy.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ScheduleSeatMapService {

	private final ScheduleRepository scheduleRepository;
	private final BookingRepository bookingRepository;

	public ScheduleSeatMapService(ScheduleRepository scheduleRepository, BookingRepository bookingRepository) {
		this.scheduleRepository = scheduleRepository;
		this.bookingRepository = bookingRepository;
	}

	@Transactional(readOnly = true)
	public ScheduleSeatsResponseDto getSeatMap(UUID scheduleId, TravelClass travelClass) {
		Schedule schedule = scheduleRepository.findWithTrainById(scheduleId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

		List<Booking> confirmedBookings = bookingRepository.findByScheduleIdAndTravelClassAndStatus(
				scheduleId,
				travelClass,
				BookingStatus.CONFIRMED
		);

		Set<String> bookedSeats = new LinkedHashSet<>();
		for (Booking booking : confirmedBookings) {
			bookedSeats.addAll(splitSeats(booking.getSeatNumbers()));
		}

		return new ScheduleSeatsResponseDto(
				scheduleId,
				travelClass,
				schedule.getTrain().getSeatsPerClass(),
				bookedSeats.stream().sorted().toList()
		);
	}

	private List<String> splitSeats(String seatsCsv) {
		if (seatsCsv == null || seatsCsv.isBlank()) {
			return List.of();
		}

		return List.of(seatsCsv.split(",")).stream()
				.map(String::trim)
				.map(String::toUpperCase)
				.filter(value -> !value.isBlank())
				.toList();
	}
}


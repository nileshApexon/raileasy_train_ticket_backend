package com.raileasy.service;

import com.raileasy.domain.Booking;
import com.raileasy.domain.BookingStatus;
import com.raileasy.domain.Schedule;
import com.raileasy.domain.TravelClass;
import com.raileasy.dto.ClassAvailabilityDto;
import com.raileasy.dto.ScheduleSearchResponseDto;
import com.raileasy.repository.BookingRepository;
import com.raileasy.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ScheduleSearchService {

    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;

    public ScheduleSearchService(ScheduleRepository scheduleRepository, BookingRepository bookingRepository) {
        this.scheduleRepository = scheduleRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<ScheduleSearchResponseDto> search(String fromStation, String toStation, LocalDate date) {
        List<Schedule> schedules = scheduleRepository
                .findByFromStationIgnoreCaseAndToStationIgnoreCaseAndJourneyDateOrderByDepartureTimeAsc(
                        fromStation.trim(),
                        toStation.trim(),
                        date
                );

        if (schedules.isEmpty()) {
            return List.of();
        }

        List<UUID> scheduleIds = schedules.stream().map(Schedule::getId).toList();
        List<Booking> confirmedBookings = bookingRepository.findByScheduleIdInAndStatus(scheduleIds, BookingStatus.CONFIRMED);

        Map<UUID, Map<TravelClass, Integer>> bookedSeatCountBySchedule = new HashMap<>();
        for (Booking booking : confirmedBookings) {
            int seatCount = countSeats(booking.getSeatNumbers());
            bookedSeatCountBySchedule
                    .computeIfAbsent(booking.getSchedule().getId(), ignored -> new EnumMap<>(TravelClass.class))
                    .merge(booking.getTravelClass(), seatCount, Integer::sum);
        }

        List<ScheduleSearchResponseDto> response = new ArrayList<>();
        for (Schedule schedule : schedules) {
            Map<TravelClass, Integer> bookedCountByClass = bookedSeatCountBySchedule.getOrDefault(
                    schedule.getId(),
                    new EnumMap<>(TravelClass.class)
            );

            int totalSeats = schedule.getTrain().getSeatsPerClass();
            List<ClassAvailabilityDto> availability = List.of(
                    toAvailability(TravelClass.SLEEPER, totalSeats, bookedCountByClass.getOrDefault(TravelClass.SLEEPER, 0), schedule.getFareSleeper()),
                    toAvailability(TravelClass.AC_3, totalSeats, bookedCountByClass.getOrDefault(TravelClass.AC_3, 0), schedule.getFareAc3()),
                    toAvailability(TravelClass.AC_2, totalSeats, bookedCountByClass.getOrDefault(TravelClass.AC_2, 0), schedule.getFareAc2())
            );

            response.add(new ScheduleSearchResponseDto(
                    schedule.getId(),
                    schedule.getTrain().getTrainNumber(),
                    schedule.getTrain().getTrainName(),
                    schedule.getFromStation(),
                    schedule.getToStation(),
                    schedule.getJourneyDate(),
                    schedule.getDepartureTime(),
                    schedule.getArrivalTime(),
                    availability
            ));
        }

        return response;
    }

    private ClassAvailabilityDto toAvailability(TravelClass travelClass, int totalSeats, int bookedSeats, BigDecimal fare) {
        return new ClassAvailabilityDto(travelClass, Math.max(totalSeats - bookedSeats, 0), fare);
    }

    private int countSeats(String seatNumbersCsv) {
        if (seatNumbersCsv == null || seatNumbersCsv.isBlank()) {
            return 0;
        }

        return (int) List.of(seatNumbersCsv.split(","))
                .stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .count();
    }
}


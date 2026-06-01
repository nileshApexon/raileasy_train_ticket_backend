package com.raileasy.service;

import com.raileasy.domain.Booking;
import com.raileasy.domain.BookingStatus;
import com.raileasy.domain.Schedule;
import com.raileasy.domain.TravelClass;
import com.raileasy.domain.User;
import com.raileasy.dto.booking.BookingResponseDto;
import com.raileasy.dto.booking.CreateBookingRequestDto;
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
public class BookingService {

    private static final int MAX_SEATS_PER_BOOKING = 4;

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;

    public BookingService(BookingRepository bookingRepository, ScheduleRepository scheduleRepository) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public BookingResponseDto create(User user, CreateBookingRequestDto request) {
        Schedule schedule = scheduleRepository.findWithTrainById(request.scheduleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid scheduleId"));


        List<String> requestedSeats = normalizeAndValidateSeats(request.seatNumbers());
        validateSeatAvailability(schedule, request.travelClass(), requestedSeats);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setTravelClass(request.travelClass());
        booking.setSeatNumbers(String.join(",", requestedSeats));
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPnrNumber(generatePnr());

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getMine(User user) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public BookingResponseDto cancel(User user, UUID bookingId) {
        Booking booking = bookingRepository.findWithUserById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return toResponse(booking);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    private void validateSeatAvailability(Schedule schedule, TravelClass travelClass, List<String> requestedSeats) {
        List<Booking> confirmedBookings = bookingRepository.findByScheduleIdAndTravelClassAndStatus(
                schedule.getId(),
                travelClass,
                BookingStatus.CONFIRMED
        );

        Set<String> bookedSeats = new LinkedHashSet<>();
        for (Booking confirmed : confirmedBookings) {
            bookedSeats.addAll(splitSeats(confirmed.getSeatNumbers()));
        }

        for (String seat : requestedSeats) {
            if (bookedSeats.contains(seat)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already booked: " + seat);
            }
        }

        int projectedBooked = bookedSeats.size() + requestedSeats.size();
        if (projectedBooked > schedule.getTrain().getSeatsPerClass()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Selected seats exceed class capacity");
        }
    }

    private List<String> normalizeAndValidateSeats(List<String> seatNumbers) {
        Set<String> normalized = new LinkedHashSet<>();
        for (String rawSeat : seatNumbers) {
            String seat = rawSeat == null ? "" : rawSeat.trim().toUpperCase();
            if (seat.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat number cannot be blank");
            }
            normalized.add(seat);
        }

        if (normalized.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one seat is required");
        }
        if (normalized.size() > MAX_SEATS_PER_BOOKING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can select up to 4 seats only");
        }

        return normalized.stream().toList();
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

    private String generatePnr() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private BookingResponseDto toResponse(Booking booking) {
        Schedule schedule = booking.getSchedule();
        return new BookingResponseDto(
                booking.getId(),
                booking.getPnrNumber(),
                booking.getStatus(),
                booking.getTravelClass(),
                splitSeats(booking.getSeatNumbers()),
                schedule.getId(),
                schedule.getTrain().getTrainNumber(),
                schedule.getTrain().getTrainName(),
                schedule.getFromStation(),
                schedule.getToStation(),
                schedule.getJourneyDate(),
                schedule.getDepartureTime(),
                schedule.getArrivalTime()
        );
    }
}


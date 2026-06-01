package com.raileasy.controller;

import com.raileasy.dto.booking.BookingResponseDto;
import com.raileasy.dto.booking.CreateBookingRequestDto;
import com.raileasy.service.BookingService;
import com.raileasy.service.UserAccessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserAccessService userAccessService;

    public BookingController(BookingService bookingService, UserAccessService userAccessService) {
        this.bookingService = bookingService;
        this.userAccessService = userAccessService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(
            @RequestHeader(UserAccessService.USER_HEADER) String userIdHeader,
            @RequestBody @Valid CreateBookingRequestDto request
    ) {
        return bookingService.create(userAccessService.requireUser(userIdHeader), request);
    }

    @GetMapping("/mine")
    public List<BookingResponseDto> getMine(@RequestHeader(UserAccessService.USER_HEADER) String userIdHeader) {
        return bookingService.getMine(userAccessService.requireUser(userIdHeader));
    }

    @PutMapping("/{bookingId}/cancel")
    public BookingResponseDto cancel(
            @RequestHeader(UserAccessService.USER_HEADER) String userIdHeader,
            @PathVariable UUID bookingId
    ) {
        return bookingService.cancel(userAccessService.requireUser(userIdHeader), bookingId);
    }
}


package com.raileasy.dto.booking;

import com.raileasy.domain.TravelClass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateBookingRequestDto(
        @NotNull UUID scheduleId,
        @NotNull TravelClass travelClass,
        @NotEmpty @Size(max = 4) List<String> seatNumbers
) {
}


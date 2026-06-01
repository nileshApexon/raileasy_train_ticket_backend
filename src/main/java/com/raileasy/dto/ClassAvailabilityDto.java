package com.raileasy.dto;

import com.raileasy.domain.TravelClass;

import java.math.BigDecimal;

public record ClassAvailabilityDto(
        TravelClass travelClass,
        int seatsLeft,
        BigDecimal fare
) {
}


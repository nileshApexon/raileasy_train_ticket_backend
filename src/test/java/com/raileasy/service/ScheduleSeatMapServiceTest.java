package com.raileasy.service;

import com.raileasy.domain.TravelClass;
import com.raileasy.dto.ScheduleSeatsResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class ScheduleSeatMapServiceTest {

    @Autowired
    private ScheduleSeatMapService scheduleSeatMapService;

    @Test
    void returnsBookedSeatsForTravelClass() {
        ScheduleSeatsResponseDto seatMap = scheduleSeatMapService.getSeatMap(
                UUID.fromString("33333333-3333-3333-3333-333333333331"),
                TravelClass.AC_3
        );

        assertThat(seatMap.totalSeats()).isEqualTo(75);
        assertThat(seatMap.bookedSeatNumbers()).containsExactly("B1/1", "B1/2");
    }

    @Test
    void excludesCancelledSeats() {
        ScheduleSeatsResponseDto seatMap = scheduleSeatMapService.getSeatMap(
                UUID.fromString("33333333-3333-3333-3333-333333333331"),
                TravelClass.SLEEPER
        );

        assertThat(seatMap.bookedSeatNumbers()).isEmpty();
    }
}


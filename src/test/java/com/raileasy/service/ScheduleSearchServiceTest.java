package com.raileasy.service;

import com.raileasy.domain.TravelClass;
import com.raileasy.dto.ScheduleSearchResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class ScheduleSearchServiceTest {

    @Autowired
    private ScheduleSearchService scheduleSearchService;

    @Test
    void searchReturnsSchedulesAndClassAvailability() {
        List<ScheduleSearchResponseDto> result = scheduleSearchService.search(
                "Chennai Central",
                "Mumbai CSMT",
                LocalDate.parse("2025-10-21")
        );

        assertThat(result).hasSize(2);

        ScheduleSearchResponseDto first = result.get(0);
        assertThat(first.trainNumber()).isEqualTo("12163");

        int ac3SeatsLeft = first.classAvailability().stream()
                .filter(value -> value.travelClass() == TravelClass.AC_3)
                .findFirst()
                .orElseThrow()
                .seatsLeft();

        // One confirmed AC_3 booking with two seats in seed data; 75 total - 2 booked = 73.
        assertThat(ac3SeatsLeft).isEqualTo(73);
    }
}


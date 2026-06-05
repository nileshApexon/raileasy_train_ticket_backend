package com.raileasy.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ScheduleSeatMapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getSeatsReturnsBookedSeatNumbers() throws Exception {
        mockMvc.perform(get("/api/schedules/33333333-3333-3333-3333-333333333331/seats")
                        .param("class", "AC_3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value("33333333-3333-3333-3333-333333333331"))
                .andExpect(jsonPath("$.travelClass").value("AC_3"))
                .andExpect(jsonPath("$.totalSeats").value(75))
                .andExpect(jsonPath("$.bookedSeatNumbers").isArray())
                .andExpect(jsonPath("$.bookedSeatNumbers[0]").value("B1/1"))
                .andExpect(jsonPath("$.bookedSeatNumbers[1]").value("B1/2"));
    }

    @Test
    void getSeatsExcludesCancelledBookings() throws Exception {
        mockMvc.perform(get("/api/schedules/33333333-3333-3333-3333-333333333331/seats")
                        .param("class", "SLEEPER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookedSeatNumbers").isEmpty());
    }

    @Test
    void getSeatsReturnsEmptyForScheduleWithNoBookings() throws Exception {
        mockMvc.perform(get("/api/schedules/33333333-3333-3333-3333-333333333332/seats")
                        .param("class", "AC_3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSeats").value(75))
                .andExpect(jsonPath("$.bookedSeatNumbers").isEmpty());
    }

    @Test
    void getSeatsReturns404ForUnknownSchedule() throws Exception {
        mockMvc.perform(get("/api/schedules/00000000-0000-0000-0000-000000000000/seats")
                        .param("class", "AC_3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSeatsReturns400ForMissingClassParam() throws Exception {
        mockMvc.perform(get("/api/schedules/33333333-3333-3333-3333-333333333331/seats"))
                .andExpect(status().isBadRequest());
    }
}

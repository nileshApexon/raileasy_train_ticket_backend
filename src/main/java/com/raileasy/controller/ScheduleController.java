package com.raileasy.controller;

import com.raileasy.dto.admin.ScheduleRequestDto;
import com.raileasy.dto.admin.ScheduleResponseDto;
import com.raileasy.dto.ScheduleSearchResponseDto;
import com.raileasy.dto.ScheduleSeatsResponseDto;
import com.raileasy.domain.TravelClass;
import com.raileasy.service.ScheduleAdminService;
import com.raileasy.service.ScheduleSeatMapService;
import com.raileasy.service.ScheduleSearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
@Validated
public class ScheduleController {

    private final ScheduleSearchService scheduleSearchService;
    private final ScheduleSeatMapService scheduleSeatMapService;
    private final ScheduleAdminService scheduleAdminService;

    public ScheduleController(
            ScheduleSearchService scheduleSearchService,
            ScheduleSeatMapService scheduleSeatMapService,
            ScheduleAdminService scheduleAdminService
    ) {
        this.scheduleSearchService = scheduleSearchService;
        this.scheduleSeatMapService = scheduleSeatMapService;
        this.scheduleAdminService = scheduleAdminService;
    }

    // Public endpoint - no authentication required (guest access)
    @GetMapping
    public List<ScheduleSearchResponseDto> search(
            @RequestParam("from") @NotBlank String from,
            @RequestParam("to") @NotBlank String to,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return scheduleSearchService.search(from, to, date);
    }

    // Public endpoint - no authentication required (guest access)
    @GetMapping("/{scheduleId}/seats")
    public ScheduleSeatsResponseDto getSeats(
            @PathVariable UUID scheduleId,
            @RequestParam("class") TravelClass travelClass
    ) {
        return scheduleSeatMapService.getSeatMap(scheduleId, travelClass);
    }

    // Admin endpoints - require ADMIN role
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ScheduleResponseDto> getAllForAdmin() {
        return scheduleAdminService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ScheduleResponseDto create(@RequestBody @Valid ScheduleRequestDto request) {
        return scheduleAdminService.create(request);
    }

    @PutMapping("/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ScheduleResponseDto update(
            @PathVariable UUID scheduleId,
            @RequestBody @Valid ScheduleRequestDto request
    ) {
        return scheduleAdminService.update(scheduleId, request);
    }

    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID scheduleId) {
        scheduleAdminService.delete(scheduleId);
    }
}


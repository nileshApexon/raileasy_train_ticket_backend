package com.raileasy.controller;

import com.raileasy.dto.admin.ScheduleRequestDto;
import com.raileasy.dto.admin.ScheduleResponseDto;
import com.raileasy.dto.ScheduleSearchResponseDto;
import com.raileasy.dto.ScheduleSeatsResponseDto;
import com.raileasy.domain.TravelClass;
import com.raileasy.service.ScheduleAdminService;
import com.raileasy.service.ScheduleSeatMapService;
import com.raileasy.service.ScheduleSearchService;
import com.raileasy.service.UserAccessService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final UserAccessService userAccessService;

    public ScheduleController(
            ScheduleSearchService scheduleSearchService,
            ScheduleSeatMapService scheduleSeatMapService,
            ScheduleAdminService scheduleAdminService,
            UserAccessService userAccessService
    ) {
        this.scheduleSearchService = scheduleSearchService;
        this.scheduleSeatMapService = scheduleSeatMapService;
        this.scheduleAdminService = scheduleAdminService;
        this.userAccessService = userAccessService;
    }

    @GetMapping
    public List<ScheduleSearchResponseDto> search(
            @RequestParam("from") @NotBlank String from,
            @RequestParam("to") @NotBlank String to,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return scheduleSearchService.search(from, to, date);
    }

    @GetMapping("/{scheduleId}/seats")
    public ScheduleSeatsResponseDto getSeats(
            @PathVariable UUID scheduleId,
            @RequestParam("class") TravelClass travelClass
    ) {
        return scheduleSeatMapService.getSeatMap(scheduleId, travelClass);
    }

    @GetMapping("/admin")
    public List<ScheduleResponseDto> getAllForAdmin(@RequestHeader(UserAccessService.USER_HEADER) String userIdHeader) {
        userAccessService.requireAdmin(userIdHeader);
        return scheduleAdminService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponseDto create(
            @RequestHeader(UserAccessService.USER_HEADER) String userIdHeader,
            @RequestBody @Valid ScheduleRequestDto request
    ) {
        userAccessService.requireAdmin(userIdHeader);
        return scheduleAdminService.create(request);
    }

    @PutMapping("/{scheduleId}")
    public ScheduleResponseDto update(
            @RequestHeader(UserAccessService.USER_HEADER) String userIdHeader,
            @PathVariable UUID scheduleId,
            @RequestBody @Valid ScheduleRequestDto request
    ) {
        userAccessService.requireAdmin(userIdHeader);
        return scheduleAdminService.update(scheduleId, request);
    }

    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(UserAccessService.USER_HEADER) String userIdHeader,
            @PathVariable UUID scheduleId
    ) {
        userAccessService.requireAdmin(userIdHeader);
        scheduleAdminService.delete(scheduleId);
    }
}


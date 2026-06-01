package com.raileasy.controller;

import com.raileasy.dto.admin.TrainRequestDto;
import com.raileasy.dto.admin.TrainResponseDto;
import com.raileasy.service.TrainAdminService;
import com.raileasy.service.UserAccessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/trains")
public class TrainAdminController {

    private final TrainAdminService trainAdminService;
    private final UserAccessService userAccessService;

    public TrainAdminController(TrainAdminService trainAdminService, UserAccessService userAccessService) {
        this.trainAdminService = trainAdminService;
        this.userAccessService = userAccessService;
    }

    @GetMapping
    public List<TrainResponseDto> getAll(@RequestHeader(UserAccessService.USER_HEADER) String userIdHeader) {
        userAccessService.requireAdmin(userIdHeader);
        return trainAdminService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainResponseDto create(
            @RequestHeader(UserAccessService.USER_HEADER) String userIdHeader,
            @RequestBody @Valid TrainRequestDto request
    ) {
        userAccessService.requireAdmin(userIdHeader);
        return trainAdminService.create(request);
    }

    @PutMapping("/{trainId}")
    public TrainResponseDto update(
            @RequestHeader(UserAccessService.USER_HEADER) String userIdHeader,
            @PathVariable UUID trainId,
            @RequestBody @Valid TrainRequestDto request
    ) {
        userAccessService.requireAdmin(userIdHeader);
        return trainAdminService.update(trainId, request);
    }

    @DeleteMapping("/{trainId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader(UserAccessService.USER_HEADER) String userIdHeader,
            @PathVariable UUID trainId
    ) {
        userAccessService.requireAdmin(userIdHeader);
        trainAdminService.delete(trainId);
    }
}


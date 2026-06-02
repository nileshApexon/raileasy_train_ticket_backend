package com.raileasy.controller;

import com.raileasy.dto.admin.TrainRequestDto;
import com.raileasy.dto.admin.TrainResponseDto;
import com.raileasy.service.TrainAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trains")
@PreAuthorize("hasRole('ADMIN')")
public class TrainAdminController {

    private final TrainAdminService trainAdminService;

    public TrainAdminController(TrainAdminService trainAdminService) {
        this.trainAdminService = trainAdminService;
    }

    @GetMapping
    public List<TrainResponseDto> getAll() {
        return trainAdminService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainResponseDto create(@RequestBody @Valid TrainRequestDto request) {
        return trainAdminService.create(request);
    }

    @PutMapping("/{trainId}")
    public TrainResponseDto update(@PathVariable UUID trainId, @RequestBody @Valid TrainRequestDto request) {
        return trainAdminService.update(trainId, request);
    }

    @DeleteMapping("/{trainId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID trainId) {
        trainAdminService.delete(trainId);
    }
}


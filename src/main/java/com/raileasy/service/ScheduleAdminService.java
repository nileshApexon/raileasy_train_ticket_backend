package com.raileasy.service;

import com.raileasy.domain.Schedule;
import com.raileasy.domain.Train;
import com.raileasy.dto.admin.ScheduleRequestDto;
import com.raileasy.dto.admin.ScheduleResponseDto;
import com.raileasy.repository.ScheduleRepository;
import com.raileasy.repository.TrainRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ScheduleAdminService {

    private final ScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;

    public ScheduleAdminService(ScheduleRepository scheduleRepository, TrainRepository trainRepository) {
        this.scheduleRepository = scheduleRepository;
        this.trainRepository = trainRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAll() {
        return scheduleRepository.findAllByOrderByJourneyDateAscDepartureTimeAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ScheduleResponseDto create(ScheduleRequestDto request) {
        Schedule schedule = new Schedule();
        applyRequest(schedule, request);
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponseDto update(UUID scheduleId, ScheduleRequestDto request) {
        Schedule schedule = scheduleRepository.findWithTrainById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        applyRequest(schedule, request);
        return toResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public void delete(UUID scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found");
        }
        scheduleRepository.deleteById(scheduleId);
    }

    private void applyRequest(Schedule schedule, ScheduleRequestDto request) {
        Train train = trainRepository.findById(request.trainId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid trainId"));

        if (request.arrivalTime().isBefore(request.departureTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "arrivalTime cannot be before departureTime");
        }

        schedule.setTrain(train);
        schedule.setFromStation(request.fromStation().trim());
        schedule.setToStation(request.toStation().trim());
        schedule.setDepartureTime(request.departureTime());
        schedule.setArrivalTime(request.arrivalTime());
        schedule.setJourneyDate(request.journeyDate());
        schedule.setFareSleeper(request.fareSleeper());
        schedule.setFareAc3(request.fareAc3());
        schedule.setFareAc2(request.fareAc2());
    }

    private ScheduleResponseDto toResponse(Schedule schedule) {
        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getTrain().getId(),
                schedule.getTrain().getTrainNumber(),
                schedule.getTrain().getTrainName(),
                schedule.getFromStation(),
                schedule.getToStation(),
                schedule.getDepartureTime(),
                schedule.getArrivalTime(),
                schedule.getJourneyDate(),
                schedule.getFareSleeper(),
                schedule.getFareAc3(),
                schedule.getFareAc2()
        );
    }
}


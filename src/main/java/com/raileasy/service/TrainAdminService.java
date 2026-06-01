package com.raileasy.service;

import com.raileasy.domain.Train;
import com.raileasy.dto.admin.TrainRequestDto;
import com.raileasy.dto.admin.TrainResponseDto;
import com.raileasy.repository.TrainRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TrainAdminService {

    private final TrainRepository trainRepository;

    public TrainAdminService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @Transactional(readOnly = true)
    public List<TrainResponseDto> getAll() {
        return trainRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public TrainResponseDto create(TrainRequestDto request) {
        Train train = new Train();
        applyRequest(train, request);
        return toResponse(trainRepository.save(train));
    }

    @Transactional
    public TrainResponseDto update(UUID trainId, TrainRequestDto request) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));
        applyRequest(train, request);
        return toResponse(trainRepository.save(train));
    }

    @Transactional
    public void delete(UUID trainId) {
        if (!trainRepository.existsById(trainId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found");
        }
        trainRepository.deleteById(trainId);
    }

    private void applyRequest(Train train, TrainRequestDto request) {
        train.setTrainNumber(request.trainNumber().trim());
        train.setTrainName(request.trainName().trim());
        train.setSeatsPerClass(request.seatsPerClass());
    }

    private TrainResponseDto toResponse(Train train) {
        return new TrainResponseDto(train.getId(), train.getTrainNumber(), train.getTrainName(), train.getSeatsPerClass());
    }
}


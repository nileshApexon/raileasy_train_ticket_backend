package com.raileasy.repository;

import com.raileasy.domain.Schedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    @EntityGraph(attributePaths = "train")
    List<Schedule> findByFromStationIgnoreCaseAndToStationIgnoreCaseAndJourneyDateOrderByDepartureTimeAsc(
            String fromStation,
            String toStation,
            LocalDate journeyDate
    );

    @EntityGraph(attributePaths = "train")
    List<Schedule> findAllByOrderByJourneyDateAscDepartureTimeAsc();

    @EntityGraph(attributePaths = "train")
    Optional<Schedule> findWithTrainById(UUID id);
}


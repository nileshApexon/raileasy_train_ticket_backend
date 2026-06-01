package com.raileasy.repository;

import com.raileasy.domain.Booking;
import com.raileasy.domain.BookingStatus;
import com.raileasy.domain.TravelClass;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByScheduleIdInAndStatus(Collection<UUID> scheduleIds, BookingStatus status);

    List<Booking> findByScheduleIdAndTravelClassAndStatus(UUID scheduleId, TravelClass travelClass, BookingStatus status);

    @EntityGraph(attributePaths = {"schedule", "schedule.train"})
    List<Booking> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"schedule", "schedule.train", "user"})
    Optional<Booking> findWithUserById(UUID id);
}


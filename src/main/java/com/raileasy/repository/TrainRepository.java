package com.raileasy.repository;

import com.raileasy.domain.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrainRepository extends JpaRepository<Train, UUID> {
}


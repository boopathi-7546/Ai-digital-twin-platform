package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.DigitalTwin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DigitalTwinRepository extends JpaRepository<DigitalTwin, Long> {

    Optional<DigitalTwin> findByStudentId(Long studentId);
}

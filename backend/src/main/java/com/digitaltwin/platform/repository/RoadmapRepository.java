package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    List<Roadmap> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    Optional<Roadmap> findByIdAndStudentId(Long id, Long studentId);
}

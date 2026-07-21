package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.RoadmapItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoadmapItemRepository extends JpaRepository<RoadmapItem, Long> {

    Optional<RoadmapItem> findByIdAndRoadmapId(Long id, Long roadmapId);
}

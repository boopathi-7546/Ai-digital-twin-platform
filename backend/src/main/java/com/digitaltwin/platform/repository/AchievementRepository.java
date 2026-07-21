package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByStudentIdOrderByAchievementDateDesc(Long studentId);

    Optional<Achievement> findByIdAndStudentId(Long id, Long studentId);
}

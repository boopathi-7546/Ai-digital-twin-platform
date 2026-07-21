package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.SkillGapAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillGapAnalysisRepository extends JpaRepository<SkillGapAnalysis, Long> {

    List<SkillGapAnalysis> findByStudentIdOrderByAnalyzedAtDesc(Long studentId);

    Optional<SkillGapAnalysis> findFirstByStudentIdAndTargetRoleIgnoreCaseOrderByAnalyzedAtDesc(
            Long studentId, String targetRole);
}

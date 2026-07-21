package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {

    Optional<ResumeAnalysis> findFirstByResumeIdOrderByAnalyzedAtDesc(Long resumeId);
}

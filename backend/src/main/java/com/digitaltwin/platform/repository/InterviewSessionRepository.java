package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    List<InterviewSession> findByStudentIdOrderByStartedAtDesc(Long studentId);

    Optional<InterviewSession> findByIdAndStudentId(Long id, Long studentId);
}

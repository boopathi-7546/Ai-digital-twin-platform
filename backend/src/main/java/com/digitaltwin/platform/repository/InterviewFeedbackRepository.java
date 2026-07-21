package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, Long> {

    Optional<InterviewFeedback> findBySessionId(Long sessionId);
}

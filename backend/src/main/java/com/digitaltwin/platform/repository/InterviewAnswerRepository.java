package com.digitaltwin.platform.repository;

import com.digitaltwin.platform.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, Long> {

    Optional<InterviewAnswer> findByInterviewQuestionId(Long interviewQuestionId);
}

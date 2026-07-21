package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.interview.*;

import java.util.List;

/**
 * Manages the lifecycle of a mock interview session: start (generate
 * questions), submit answers one at a time, and complete (trigger AI
 * evaluation via InterviewEvaluationService).
 */
public interface MockInterviewService {

    InterviewSessionResponse startSession(Long userId, StartInterviewRequest request);

    InterviewSessionResponse getSession(Long userId, Long sessionId);

    List<InterviewSessionResponse> getMySessions(Long userId);

    InterviewQuestionResponse submitAnswer(Long userId, Long sessionId, SubmitAnswerRequest request);

    InterviewFeedbackResponse completeSession(Long userId, Long sessionId);

    InterviewFeedbackResponse getFeedback(Long userId, Long sessionId);
}

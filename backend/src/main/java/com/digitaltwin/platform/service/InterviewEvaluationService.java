package com.digitaltwin.platform.service;

import com.digitaltwin.platform.entity.InterviewSession;

/**
 * Generates AI feedback for a completed interview session by sending
 * its question/answer pairs to Gemini, and persists the result as an
 * InterviewFeedback row attached to the session.
 */
public interface InterviewEvaluationService {

    void evaluateSession(InterviewSession session);
}

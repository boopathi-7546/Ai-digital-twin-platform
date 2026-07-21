package com.digitaltwin.platform.service;

import java.util.List;

/**
 * Generates mock interview questions via Gemini for a given target
 * role, count, and difficulty. Used by MockInterviewService to fill
 * out a session beyond what's available in the static question_bank.
 */
public interface InterviewGeneratorService {

    List<String> generateQuestions(String targetRole, int count, String difficulty);
}

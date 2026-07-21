package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.admin.QuestionBankRequest;
import com.digitaltwin.platform.dto.interview.InterviewSessionResponse;
import com.digitaltwin.platform.dto.interview.QuestionBankResponse;

import java.util.List;

/**
 * Admin management of the static question_bank catalog, plus
 * read-only visibility into all students' interview sessions.
 */
public interface AdminInterviewService {

    QuestionBankResponse createQuestion(QuestionBankRequest request);

    QuestionBankResponse updateQuestion(Long questionId, QuestionBankRequest request);

    void deleteQuestion(Long questionId);

    List<InterviewSessionResponse> getAllSessions();

    InterviewSessionResponse getSessionById(Long sessionId);
}

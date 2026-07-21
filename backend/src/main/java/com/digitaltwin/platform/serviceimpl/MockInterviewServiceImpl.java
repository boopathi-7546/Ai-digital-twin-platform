package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.interview.*;
import com.digitaltwin.platform.entity.*;
import com.digitaltwin.platform.exception.BadRequestException;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.*;
import com.digitaltwin.platform.service.InterviewEvaluationService;
import com.digitaltwin.platform.service.InterviewGeneratorService;
import com.digitaltwin.platform.service.MockInterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the mock interview flow. Sessions are generated purely
 * from AI questions for the given target role/difficulty (the static
 * question_bank is used elsewhere for admin-curated practice sets);
 * this keeps each session tailored to what the student asked for.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MockInterviewServiceImpl implements MockInterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewAnswerRepository interviewAnswerRepository;
    private final InterviewFeedbackRepository interviewFeedbackRepository;
    private final StudentRepository studentRepository;
    private final InterviewGeneratorService interviewGeneratorService;
    private final InterviewEvaluationService interviewEvaluationService;

    @Override
    @Transactional
    public InterviewSessionResponse startSession(Long userId, StartInterviewRequest request) {
        Student student = requireStudent(userId);

        List<String> generatedQuestions = interviewGeneratorService.generateQuestions(
                request.getTargetRole(), request.getQuestionCount(), request.getDifficulty());

        InterviewSession session = InterviewSession.builder()
                .student(student)
                .targetRole(request.getTargetRole())
                .status(InterviewSession.Status.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();
        interviewSessionRepository.save(session);

        int sequence = 1;
        for (String questionText : generatedQuestions) {
            InterviewQuestion question = InterviewQuestion.builder()
                    .session(session)
                    .questionText(questionText)
                    .sequenceNo(sequence++)
                    .build();
            interviewQuestionRepository.save(question);
            session.getQuestions().add(question);
        }

        log.info("Interview session started for studentId={}: sessionId={}, questions={}",
                student.getId(), session.getId(), generatedQuestions.size());

        return toSessionResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewSessionResponse getSession(Long userId, Long sessionId) {
        InterviewSession session = requireSession(userId, sessionId);
        return toSessionResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewSessionResponse> getMySessions(Long userId) {
        Student student = requireStudent(userId);
        return interviewSessionRepository.findByStudentIdOrderByStartedAtDesc(student.getId())
                .stream()
                .map(this::toSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InterviewQuestionResponse submitAnswer(Long userId, Long sessionId, SubmitAnswerRequest request) {
        InterviewSession session = requireSession(userId, sessionId);

        if (session.getStatus() != InterviewSession.Status.IN_PROGRESS) {
            throw new BadRequestException("This interview session is not currently in progress.");
        }

        InterviewQuestion question = interviewQuestionRepository
                .findByIdAndSessionId(request.getQuestionId(), sessionId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Interview question", request.getQuestionId()));

        InterviewAnswer answer = interviewAnswerRepository.findByInterviewQuestionId(question.getId())
                .orElseGet(() -> InterviewAnswer.builder().interviewQuestion(question).build());

        answer.setAnswerText(request.getAnswerText());
        answer.setAnswerDurationSeconds(request.getAnswerDurationSeconds());
        answer.setSubmittedAt(LocalDateTime.now());
        interviewAnswerRepository.save(answer);
        question.setAnswer(answer);

        return toQuestionResponse(question);
    }

    @Override
    @Transactional
    public InterviewFeedbackResponse completeSession(Long userId, Long sessionId) {
        InterviewSession session = requireSession(userId, sessionId);

        if (session.getStatus() == InterviewSession.Status.COMPLETED) {
            throw new BadRequestException("This interview session has already been completed.");
        }

        session.setCompletedAt(LocalDateTime.now());
        interviewSessionRepository.save(session);

        interviewEvaluationService.evaluateSession(session);

        InterviewFeedback feedback = interviewFeedbackRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalStateException("Feedback generation did not persist as expected."));

        return toFeedbackResponse(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewFeedbackResponse getFeedback(Long userId, Long sessionId) {
        requireSession(userId, sessionId);
        InterviewFeedback feedback = interviewFeedbackRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "This session has not been completed/evaluated yet."));
        return toFeedbackResponse(feedback);
    }

    // ---------- Helpers ----------

    private Student requireStudent(Long userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Student profile for user", userId));
    }

    private InterviewSession requireSession(Long userId, Long sessionId) {
        Student student = requireStudent(userId);
        return interviewSessionRepository.findByIdAndStudentId(sessionId, student.getId())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Interview session", sessionId));
    }

    private InterviewSessionResponse toSessionResponse(InterviewSession session) {
        List<InterviewQuestionResponse> questionResponses = session.getQuestions().stream()
                .map(this::toQuestionResponse)
                .collect(Collectors.toList());

        return InterviewSessionResponse.builder()
                .sessionId(session.getId())
                .targetRole(session.getTargetRole())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .overallScore(session.getOverallScore())
                .questions(questionResponses)
                .build();
    }

    private InterviewQuestionResponse toQuestionResponse(InterviewQuestion question) {
        InterviewAnswer answer = question.getAnswer();
        return InterviewQuestionResponse.builder()
                .questionId(question.getId())
                .questionText(question.getQuestionText())
                .sequenceNo(question.getSequenceNo())
                .answerText(answer != null ? answer.getAnswerText() : null)
                .answerDurationSeconds(answer != null ? answer.getAnswerDurationSeconds() : null)
                .answered(answer != null)
                .build();
    }

    private InterviewFeedbackResponse toFeedbackResponse(InterviewFeedback feedback) {
        return InterviewFeedbackResponse.builder()
                .sessionId(feedback.getSession().getId())
                .confidenceScore(feedback.getConfidenceScore())
                .communicationScore(feedback.getCommunicationScore())
                .technicalScore(feedback.getTechnicalScore())
                .strengths(jsonArrayToList(feedback.getStrengths()))
                .weaknesses(jsonArrayToList(feedback.getWeaknesses()))
                .detailedFeedback(feedback.getDetailedFeedback())
                .generatedAt(feedback.getGeneratedAt())
                .build();
    }

    private List<String> jsonArrayToList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(json);
            List<String> result = new java.util.ArrayList<>();
            node.forEach(n -> result.add(n.asText()));
            return result;
        } catch (Exception ex) {
            return List.of();
        }
    }
}

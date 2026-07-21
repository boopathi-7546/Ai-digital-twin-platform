package com.digitaltwin.platform.serviceimpl;

import com.digitaltwin.platform.dto.admin.QuestionBankRequest;
import com.digitaltwin.platform.dto.interview.InterviewQuestionResponse;
import com.digitaltwin.platform.dto.interview.InterviewSessionResponse;
import com.digitaltwin.platform.dto.interview.QuestionBankResponse;
import com.digitaltwin.platform.entity.InterviewAnswer;
import com.digitaltwin.platform.entity.InterviewQuestion;
import com.digitaltwin.platform.entity.InterviewSession;
import com.digitaltwin.platform.entity.QuestionBank;
import com.digitaltwin.platform.exception.ResourceNotFoundException;
import com.digitaltwin.platform.repository.InterviewSessionRepository;
import com.digitaltwin.platform.repository.QuestionBankRepository;
import com.digitaltwin.platform.service.AdminInterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminInterviewServiceImpl implements AdminInterviewService {

    private final QuestionBankRepository questionBankRepository;
    private final InterviewSessionRepository interviewSessionRepository;

    @Override
    @Transactional
    public QuestionBankResponse createQuestion(QuestionBankRequest request) {
        QuestionBank question = QuestionBank.builder()
                .questionText(request.getQuestionText())
                .category(request.getCategory())
                .role(request.getRole())
                .difficulty(request.getDifficulty())
                .aiGenerated(false)
                .build();

        questionBankRepository.save(question);
        return toQuestionBankResponse(question);
    }

    @Override
    @Transactional
    public QuestionBankResponse updateQuestion(Long questionId, QuestionBankRequest request) {
        QuestionBank question = questionBankRepository.findById(questionId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Question", questionId));

        question.setQuestionText(request.getQuestionText());
        question.setCategory(request.getCategory());
        question.setRole(request.getRole());
        question.setDifficulty(request.getDifficulty());

        questionBankRepository.save(question);
        return toQuestionBankResponse(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        QuestionBank question = questionBankRepository.findById(questionId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Question", questionId));
        questionBankRepository.delete(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewSessionResponse> getAllSessions() {
        return interviewSessionRepository.findAll().stream()
                .map(this::toSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewSessionResponse getSessionById(Long sessionId) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Interview session", sessionId));
        return toSessionResponse(session);
    }

    private QuestionBankResponse toQuestionBankResponse(QuestionBank question) {
        return QuestionBankResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .category(question.getCategory())
                .role(question.getRole())
                .difficulty(question.getDifficulty())
                .aiGenerated(question.isAiGenerated())
                .build();
    }

    private InterviewSessionResponse toSessionResponse(InterviewSession session) {
        List<InterviewQuestionResponse> questions = session.getQuestions().stream()
                .map(this::toInterviewQuestionResponse)
                .collect(Collectors.toList());

        return InterviewSessionResponse.builder()
                .sessionId(session.getId())
                .targetRole(session.getTargetRole())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .overallScore(session.getOverallScore())
                .questions(questions)
                .build();
    }

    private InterviewQuestionResponse toInterviewQuestionResponse(InterviewQuestion question) {
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
}

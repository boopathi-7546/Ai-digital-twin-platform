import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, Send, CheckCircle2, Sparkles } from 'lucide-react';
import interviewService from '../../services/interviewService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';

export function InterviewSession() {
  const { sessionId } = useParams();
  const navigate = useNavigate();
  const notify = useNotification();
  const { data: session, loading, refetch, setData } = useFetch(
    () => interviewService.getSession(sessionId), [sessionId]
  );
  const [answers, setAnswers] = useState({});
  const [submittingId, setSubmittingId] = useState(null);
  const [completing, setCompleting] = useState(false);
  const [feedback, setFeedback] = useState(null);

  if (loading) return <PageLoader label="Loading your session…" />;
  if (!session) return null;

  const isCompleted = session.status === 'COMPLETED';

  const handleAnswerChange = (questionId, value) => {
    setAnswers((prev) => ({ ...prev, [questionId]: value }));
  };

  const handleSubmitAnswer = async (question) => {
    const answerText = answers[question.questionId] ?? question.answerText ?? '';
    if (!answerText.trim()) {
      notify.error('Please write an answer before submitting.');
      return;
    }
    setSubmittingId(question.questionId);
    try {
      const updated = await interviewService.submitAnswer(sessionId, {
        questionId: question.questionId,
        answerText,
      });
      setData((prev) => ({
        ...prev,
        questions: prev.questions.map((q) => (q.questionId === updated.questionId ? updated : q)),
      }));
      notify.success(`Answer saved for question ${question.sequenceNo}.`);
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not save your answer.');
    } finally {
      setSubmittingId(null);
    }
  };

  const handleComplete = async () => {
    const unanswered = session.questions.filter((q) => !q.answered);
    if (unanswered.length > 0) {
      notify.error(`Please answer all questions before completing (${unanswered.length} remaining).`);
      return;
    }
    setCompleting(true);
    try {
      const result = await interviewService.completeSession(sessionId);
      setFeedback(result);
      refetch();
      notify.success('Interview completed. Here is your feedback.');
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not complete this session.');
    } finally {
      setCompleting(false);
    }
  };

  return (
    <div className="space-y-6">
      <button onClick={() => navigate('/student/interview')} className="flex items-center gap-1.5 text-sm text-ink-500 hover:text-ink-100">
        <ArrowLeft className="h-4 w-4" /> Back to sessions
      </button>

      <div className="flex items-center justify-between">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink-100">{session.targetRole}</h1>
          <p className="mt-1 text-sm text-ink-500">{session.questions.length} questions</p>
        </div>
        <Badge tone={session.status === 'COMPLETED' ? 'success' : 'warning'}>{session.status.replace('_', ' ')}</Badge>
      </div>

      <div className="space-y-4">
        {session.questions.map((q) => (
          <GlassCard key={q.questionId} className="p-5">
            <p className="text-xs text-ink-700">Question {q.sequenceNo}</p>
            <p className="mt-1 font-medium text-ink-100">{q.questionText}</p>
            <textarea
              rows={3}
              className="input-field mt-3"
              placeholder="Type your answer…"
              disabled={isCompleted}
              defaultValue={q.answerText || ''}
              onChange={(e) => handleAnswerChange(q.questionId, e.target.value)}
            />
            {!isCompleted && (
              <div className="mt-3 flex items-center gap-3">
                <Button
                  size="sm"
                  onClick={() => handleSubmitAnswer(q)}
                  loading={submittingId === q.questionId}
                >
                  <Send className="h-3.5 w-3.5" /> Save answer
                </Button>
                {q.answered && <span className="flex items-center gap-1 text-xs text-success"><CheckCircle2 className="h-3.5 w-3.5" /> Saved</span>}
              </div>
            )}
          </GlassCard>
        ))}
      </div>

      {!isCompleted && (
        <div className="flex justify-end">
          <Button onClick={handleComplete} loading={completing}>
            <Sparkles className="h-4 w-4" /> Complete & get AI feedback
          </Button>
        </div>
      )}

      {(feedback || isCompleted) && (
        <FeedbackPanel sessionId={sessionId} preloaded={feedback} />
      )}
    </div>
  );
}

function FeedbackPanel({ sessionId, preloaded }) {
  const { data: feedback, loading } = useFetch(
    () => (preloaded ? Promise.resolve(preloaded) : interviewService.getFeedback(sessionId)),
    [sessionId, preloaded]
  );

  if (loading) return <PageLoader label="Loading feedback…" />;
  if (!feedback) return null;

  return (
    <GlassCard className="space-y-5">
      <h2 className="font-display text-lg font-semibold text-ink-100">AI Feedback</h2>

      <div className="grid grid-cols-3 gap-4">
        <ScoreBlock label="Confidence" value={feedback.confidenceScore} />
        <ScoreBlock label="Communication" value={feedback.communicationScore} />
        <ScoreBlock label="Technical" value={feedback.technicalScore} />
      </div>

      {feedback.strengths?.length > 0 && (
        <div>
          <p className="mb-2 text-sm font-medium text-success">Strengths</p>
          <ul className="space-y-1 text-sm text-ink-300">
            {feedback.strengths.map((s, i) => <li key={i}>• {s}</li>)}
          </ul>
        </div>
      )}

      {feedback.weaknesses?.length > 0 && (
        <div>
          <p className="mb-2 text-sm font-medium text-warning">Areas to improve</p>
          <ul className="space-y-1 text-sm text-ink-300">
            {feedback.weaknesses.map((s, i) => <li key={i}>• {s}</li>)}
          </ul>
        </div>
      )}

      {feedback.detailedFeedback && (
        <div>
          <p className="mb-2 text-sm font-medium text-ink-300">Detailed feedback</p>
          <p className="whitespace-pre-line text-sm text-ink-300">{feedback.detailedFeedback}</p>
        </div>
      )}

      <Link to="/student/roadmap" className="inline-block text-sm text-twin-cyan hover:underline">
        See how this affects your roadmap →
      </Link>
    </GlassCard>
  );
}

function ScoreBlock({ label, value }) {
  return (
    <div className="rounded-lg border border-white/10 bg-white/[0.03] p-4 text-center">
      <p className="text-xs text-ink-500">{label}</p>
      <p className="font-display text-2xl font-semibold text-twin-violet">{value ?? '—'}</p>
    </div>
  );
}

export default InterviewSession;

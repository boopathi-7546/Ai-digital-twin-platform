import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { MessagesSquare, Play } from 'lucide-react';
import interviewService from '../../services/interviewService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { Modal } from '../../components/common/Modal.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

const STATUS_TONES = { IN_PROGRESS: 'warning', COMPLETED: 'success', ABANDONED: 'neutral' };

export function Interview() {
  const { data: sessions, loading, refetch } = useFetch(() => interviewService.getMySessions(), []);
  const [modalOpen, setModalOpen] = useState(false);
  const { register, handleSubmit, formState: { isSubmitting } } = useForm({
    defaultValues: { targetRole: '', questionCount: 5, difficulty: 'MEDIUM' },
  });
  const notify = useNotification();
  const navigate = useNavigate();

  const onSubmit = async (formData) => {
    try {
      const session = await interviewService.startSession({
        ...formData,
        questionCount: Number(formData.questionCount),
      });
      setModalOpen(false);
      notify.success('Interview session started. Good luck!');
      navigate(`/student/interview/${session.sessionId}`);
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not start a new interview session.');
    }
  };

  if (loading) return <PageLoader label="Loading your interview sessions…" />;

  return (
    <div className="space-y-6">
      <div className="flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink-100">Mock interviews</h1>
          <p className="mt-1 text-sm text-ink-500">Practice with AI-generated questions for any target role.</p>
        </div>
        <Button onClick={() => setModalOpen(true)}><Play className="h-4 w-4" /> Start new session</Button>
      </div>

      {!sessions?.length ? (
        <GlassCard>
          <EmptyState
            icon={MessagesSquare}
            title="No interview sessions yet"
            description="Start your first mock interview to get AI feedback on confidence, communication, and technical depth."
            actionLabel="Start a session"
            onAction={() => setModalOpen(true)}
          />
        </GlassCard>
      ) : (
        <div className="space-y-3">
          {sessions.map((s) => (
            <GlassCard
              key={s.sessionId}
              hover
              className="flex cursor-pointer items-center justify-between p-5"
              onClick={() => navigate(`/student/interview/${s.sessionId}`)}
            >
              <div>
                <p className="font-medium text-ink-100">{s.targetRole}</p>
                <p className="text-xs text-ink-500">
                  {new Date(s.startedAt).toLocaleString()} · {s.questions?.length || 0} questions
                </p>
              </div>
              <div className="flex items-center gap-3">
                {s.overallScore != null && (
                  <span className="font-display text-lg font-semibold text-twin-violet">{s.overallScore}</span>
                )}
                <Badge tone={STATUS_TONES[s.status]}>{s.status.replace('_', ' ')}</Badge>
              </div>
            </GlassCard>
          ))}
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Start a new mock interview">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Target role</label>
            <input className="input-field" placeholder="e.g. Backend Developer" {...register('targetRole', { required: true })} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label-field">Number of questions</label>
              <input type="number" min={3} max={15} className="input-field" {...register('questionCount')} />
            </div>
            <div>
              <label className="label-field">Difficulty</label>
              <select className="input-field" {...register('difficulty')}>
                <option value="EASY">Easy</option>
                <option value="MEDIUM">Medium</option>
                <option value="HARD">Hard</option>
              </select>
            </div>
          </div>
          <Button type="submit" loading={isSubmitting} className="w-full">
            <Play className="h-4 w-4" /> Start session
          </Button>
        </form>
      </Modal>
    </div>
  );
}

export default Interview;

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import clsx from 'clsx';
import { Plus, Trash2, Pencil, ClipboardList } from 'lucide-react';
import interviewService from '../../services/interviewService.js';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { Modal } from '../../components/common/Modal.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

const DIFFICULTY_TONES = { EASY: 'success', MEDIUM: 'warning', HARD: 'danger' };

export function Interviews() {
  const [tab, setTab] = useState('questions');

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Interviews</h1>
        <p className="mt-1 text-sm text-ink-500">Manage the curated question bank and review student sessions.</p>
      </div>

      <div className="flex gap-2 border-b border-white/10 pb-3">
        {['questions', 'sessions'].map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={clsx(
              'rounded-lg px-3.5 py-2 text-sm font-medium capitalize transition-colors',
              tab === t ? 'bg-twin-gradient-soft text-ink-100 border border-twin-violet/30' : 'text-ink-500 hover:text-ink-100'
            )}
          >
            {t}
          </button>
        ))}
      </div>

      {tab === 'questions' ? <QuestionBankTab /> : <SessionsTab />}
    </div>
  );
}

function QuestionBankTab() {
  const { data: questions, loading, refetch } = useFetch(() => interviewService.getQuestionBank(), []);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const notify = useNotification();
  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm();

  const openAdd = () => {
    setEditing(null);
    reset({ questionText: '', category: '', role: '', difficulty: 'MEDIUM' });
    setModalOpen(true);
  };

  const openEdit = (q) => {
    setEditing(q);
    reset(q);
    setModalOpen(true);
  };

  const onSubmit = async (formData) => {
    try {
      if (editing) {
        await adminService.updateQuestion(editing.id, formData);
        notify.success('Question updated.');
      } else {
        await adminService.createQuestion(formData);
        notify.success('Question added.');
      }
      setModalOpen(false);
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not save this question.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await adminService.deleteQuestion(id);
      notify.success('Question deleted.');
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not delete this question.');
    }
  };

  if (loading) return <PageLoader label="Loading question bank…" />;

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button size="sm" onClick={openAdd}><Plus className="h-4 w-4" /> Add question</Button>
      </div>

      {!questions?.length ? (
        <GlassCard><EmptyState icon={ClipboardList} title="No questions yet" /></GlassCard>
      ) : (
        <div className="space-y-2">
          {questions.map((q) => (
            <GlassCard key={q.id} className="flex items-start justify-between p-4">
              <div>
                <p className="text-sm text-ink-100">{q.questionText}</p>
                <div className="mt-2 flex items-center gap-2 text-xs">
                  {q.category && <Badge tone="neutral">{q.category}</Badge>}
                  {q.role && <Badge tone="cyan">{q.role}</Badge>}
                  <Badge tone={DIFFICULTY_TONES[q.difficulty]}>{q.difficulty}</Badge>
                </div>
              </div>
              <div className="flex flex-shrink-0 gap-2">
                <button onClick={() => openEdit(q)} className="text-ink-500 hover:text-ink-100"><Pencil className="h-4 w-4" /></button>
                <button onClick={() => handleDelete(q.id)} className="text-ink-500 hover:text-danger"><Trash2 className="h-4 w-4" /></button>
              </div>
            </GlassCard>
          ))}
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit question' : 'Add question'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Question text</label>
            <textarea rows={3} className="input-field" {...register('questionText', { required: true })} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label-field">Category</label>
              <input className="input-field" placeholder="Technical / HR / Behavioral" {...register('category')} />
            </div>
            <div>
              <label className="label-field">Role</label>
              <input className="input-field" placeholder="Backend Developer" {...register('role')} />
            </div>
          </div>
          <div>
            <label className="label-field">Difficulty</label>
            <select className="input-field" {...register('difficulty')}>
              <option value="EASY">Easy</option>
              <option value="MEDIUM">Medium</option>
              <option value="HARD">Hard</option>
            </select>
          </div>
          <Button type="submit" loading={isSubmitting} className="w-full">Save</Button>
        </form>
      </Modal>
    </div>
  );
}

function SessionsTab() {
  const { data: sessions, loading } = useFetch(() => adminService.getAllSessions(), []);

  if (loading) return <PageLoader label="Loading sessions…" />;
  if (!sessions?.length) return <GlassCard><EmptyState title="No interview sessions yet" /></GlassCard>;

  return (
    <div className="space-y-2">
      {sessions.map((s) => (
        <GlassCard key={s.sessionId} className="flex items-center justify-between p-4">
          <div>
            <p className="font-medium text-ink-100">{s.targetRole}</p>
            <p className="text-xs text-ink-500">{new Date(s.startedAt).toLocaleString()} · {s.questions?.length || 0} questions</p>
          </div>
          <div className="flex items-center gap-3">
            {s.overallScore != null && <span className="font-display text-lg font-semibold text-twin-violet">{s.overallScore}</span>}
            <Badge tone={s.status === 'COMPLETED' ? 'success' : 'warning'}>{s.status.replace('_', ' ')}</Badge>
          </div>
        </GlassCard>
      ))}
    </div>
  );
}

export default Interviews;

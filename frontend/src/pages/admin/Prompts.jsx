import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { MessageSquareCode, Pencil } from 'lucide-react';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { Modal } from '../../components/common/Modal.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

export function Prompts() {
  const { data: prompts, loading, refetch } = useFetch(() => adminService.getAllPrompts(), []);
  const [editing, setEditing] = useState(null);
  const notify = useNotification();
  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm();

  const openEdit = (prompt) => {
    setEditing(prompt);
    reset(prompt);
  };

  const onSubmit = async (formData) => {
    try {
      await adminService.updatePrompt(editing.promptKey, formData);
      notify.success('Prompt template updated.');
      setEditing(null);
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not update this prompt.');
    }
  };

  if (loading) return <PageLoader label="Loading prompt templates…" />;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">AI prompt templates</h1>
        <p className="mt-1 text-sm text-ink-500">
          Tune the wording sent to Gemini for each AI feature without a code deploy.
        </p>
      </div>

      {!prompts?.length ? (
        <GlassCard><EmptyState icon={MessageSquareCode} title="No prompt templates found" /></GlassCard>
      ) : (
        <div className="space-y-3">
          {prompts.map((p) => (
            <GlassCard key={p.id} className="p-5">
              <div className="flex items-start justify-between">
                <div>
                  <p className="font-mono text-sm font-medium text-twin-cyan">{p.promptKey}</p>
                  {p.description && <p className="mt-1 text-xs text-ink-500">{p.description}</p>}
                </div>
                <div className="flex items-center gap-2">
                  <Badge tone={p.active ? 'success' : 'neutral'}>{p.active ? 'Active' : 'Inactive'}</Badge>
                  <button onClick={() => openEdit(p)} className="text-ink-500 hover:text-ink-100">
                    <Pencil className="h-4 w-4" />
                  </button>
                </div>
              </div>
              <p className="mt-3 max-h-24 overflow-hidden rounded-lg border border-white/5 bg-white/[0.02] p-3 text-xs text-ink-500">
                {p.promptTemplate}
              </p>
              <p className="mt-2 text-xs text-ink-700">
                Last updated {new Date(p.updatedAt).toLocaleString()} by {p.updatedBy || 'SYSTEM'}
              </p>
            </GlassCard>
          ))}
        </div>
      )}

      <Modal open={!!editing} onClose={() => setEditing(null)} title={editing?.promptKey} size="lg">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Prompt template</label>
            <textarea rows={8} className="input-field font-mono text-xs" {...register('promptTemplate', { required: true })} />
            <p className="mt-1 text-xs text-ink-700">
              Use {'{{placeholder}}'} tokens — they're filled in with runtime values before calling Gemini.
            </p>
          </div>
          <div>
            <label className="label-field">Description</label>
            <input className="input-field" {...register('description')} />
          </div>
          <label className="flex items-center gap-2 text-sm text-ink-300">
            <input type="checkbox" className="h-4 w-4 rounded border-white/20 bg-white/5" {...register('active')} />
            Active
          </label>
          <Button type="submit" loading={isSubmitting} className="w-full">Save changes</Button>
        </form>
      </Modal>
    </div>
  );
}

export default Prompts;

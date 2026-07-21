import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Plus, Pencil, Trash2, Wrench } from 'lucide-react';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { Modal } from '../../components/common/Modal.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

export function Skills() {
  const { data: skills, loading, refetch } = useFetch(() => adminService.getAllSkills(), []);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const notify = useNotification();
  const { register, handleSubmit, reset, formState: { isSubmitting } } = useForm();

  const openAdd = () => {
    setEditing(null);
    reset({ name: '', category: '', description: '', active: true });
    setModalOpen(true);
  };

  const openEdit = (skill) => {
    setEditing(skill);
    reset(skill);
    setModalOpen(true);
  };

  const onSubmit = async (formData) => {
    try {
      if (editing) {
        await adminService.updateSkill(editing.id, formData);
        notify.success('Skill updated.');
      } else {
        await adminService.createSkill(formData);
        notify.success('Skill added.');
      }
      setModalOpen(false);
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not save this skill.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await adminService.deleteSkill(id);
      notify.success('Skill deleted.');
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not delete this skill.');
    }
  };

  if (loading) return <PageLoader label="Loading skills catalog…" />;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink-100">Skills catalog</h1>
          <p className="mt-1 text-sm text-ink-500">The master list students choose from on their profile.</p>
        </div>
        <Button size="sm" onClick={openAdd}><Plus className="h-4 w-4" /> Add skill</Button>
      </div>

      {!skills?.length ? (
        <GlassCard><EmptyState icon={Wrench} title="No skills in the catalog yet" /></GlassCard>
      ) : (
        <div className="overflow-x-auto rounded-xl border border-white/10">
          <table className="w-full text-sm">
            <thead className="bg-white/[0.03] text-left text-xs uppercase tracking-wide text-ink-500">
              <tr>
                <th className="px-4 py-3">ID</th>
                <th className="px-4 py-3">Name</th>
                <th className="px-4 py-3">Category</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3" />
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {skills.map((s) => (
                <tr key={s.id} className="hover:bg-white/[0.02]">
                  <td className="px-4 py-3 text-ink-500">{s.id}</td>
                  <td className="px-4 py-3 font-medium text-ink-100">{s.name}</td>
                  <td className="px-4 py-3 text-ink-300">{s.category || '—'}</td>
                  <td className="px-4 py-3"><Badge tone={s.active ? 'success' : 'neutral'}>{s.active ? 'Active' : 'Inactive'}</Badge></td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex justify-end gap-2">
                      <button onClick={() => openEdit(s)} className="text-ink-500 hover:text-ink-100"><Pencil className="h-4 w-4" /></button>
                      <button onClick={() => handleDelete(s.id)} className="text-ink-500 hover:text-danger"><Trash2 className="h-4 w-4" /></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit skill' : 'Add skill'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="label-field">Name</label>
            <input className="input-field" {...register('name', { required: true })} />
          </div>
          <div>
            <label className="label-field">Category</label>
            <input className="input-field" placeholder="Programming / Framework / Soft Skill" {...register('category')} />
          </div>
          <div>
            <label className="label-field">Description</label>
            <textarea rows={2} className="input-field" {...register('description')} />
          </div>
          <label className="flex items-center gap-2 text-sm text-ink-300">
            <input type="checkbox" className="h-4 w-4 rounded border-white/20 bg-white/5" {...register('active')} />
            Active
          </label>
          <Button type="submit" loading={isSubmitting} className="w-full">Save</Button>
        </form>
      </Modal>
    </div>
  );
}

export default Skills;

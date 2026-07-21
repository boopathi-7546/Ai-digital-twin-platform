import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { TrendingUp, Sparkles, Map } from 'lucide-react';
import studentService from '../../services/studentService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

export function SkillGap() {
  const { data: analyses, loading, refetch } = useFetch(() => studentService.getSkillGapAnalyses(), []);
  const { register, handleSubmit, formState: { isSubmitting } } = useForm();
  const [generatingId, setGeneratingId] = useState(null);
  const notify = useNotification();
  const navigate = useNavigate();

  const onSubmit = async ({ targetRole }) => {
    try {
      await studentService.analyzeSkillGap(targetRole);
      notify.success(`Skill gap analysis complete for ${targetRole}.`);
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not run the skill gap analysis.');
    }
  };

  const handleGenerateRoadmap = async (analysisId) => {
    setGeneratingId(analysisId);
    try {
      const roadmap = await studentService.generateRoadmap(analysisId);
      notify.success('Roadmap generated.');
      navigate(`/student/roadmap`, { state: { highlightId: roadmap.id } });
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not generate a roadmap.');
    } finally {
      setGeneratingId(null);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Skill gap analysis</h1>
        <p className="mt-1 text-sm text-ink-500">See how your current skills compare to a target role.</p>
      </div>

      <GlassCard>
        <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4 sm:flex-row sm:items-end">
          <div className="flex-1">
            <label className="label-field">Target role</label>
            <input className="input-field" placeholder="e.g. Backend Developer" {...register('targetRole', { required: true })} />
          </div>
          <Button type="submit" loading={isSubmitting}>
            <Sparkles className="h-4 w-4" /> Analyze
          </Button>
        </form>
      </GlassCard>

      {loading ? (
        <PageLoader label="Loading your analyses…" />
      ) : !analyses?.length ? (
        <GlassCard>
          <EmptyState icon={TrendingUp} title="No analyses yet" description="Run your first skill gap analysis above." />
        </GlassCard>
      ) : (
        <div className="space-y-4">
          {analyses.map((a) => (
            <GlassCard key={a.id} className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium text-ink-100">{a.targetRole}</p>
                  <p className="text-xs text-ink-500">{new Date(a.analyzedAt).toLocaleString()}</p>
                </div>
                <div className="text-right">
                  <p className="text-xs text-ink-500">Match</p>
                  <p className="font-display text-2xl font-semibold text-twin-violet">{a.matchPercentage ?? '—'}%</p>
                </div>
              </div>

              {a.matchedSkills?.length > 0 && (
                <div>
                  <p className="mb-2 text-sm font-medium text-success">Matched skills</p>
                  <div className="flex flex-wrap gap-2">
                    {a.matchedSkills.map((s) => <Badge key={s} tone="success">{s}</Badge>)}
                  </div>
                </div>
              )}

              {a.missingSkills?.length > 0 && (
                <div>
                  <p className="mb-2 text-sm font-medium text-warning">Missing skills</p>
                  <div className="flex flex-wrap gap-2">
                    {a.missingSkills.map((s) => <Badge key={s} tone="warning">{s}</Badge>)}
                  </div>
                </div>
              )}

              <div className="flex justify-end">
                <Button size="sm" variant="secondary" onClick={() => handleGenerateRoadmap(a.id)} loading={generatingId === a.id}>
                  <Map className="h-3.5 w-3.5" /> Generate roadmap
                </Button>
              </div>
            </GlassCard>
          ))}
        </div>
      )}
    </div>
  );
}

export default SkillGap;

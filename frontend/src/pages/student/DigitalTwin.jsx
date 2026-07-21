import { useState } from 'react';
import { Bot, RefreshCcw } from 'lucide-react';
import studentService from '../../services/studentService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

function InsightList({ title, data }) {
  if (!data || Object.keys(data).length === 0) return null;
  return (
    <GlassCard>
      <h3 className="font-display text-base font-semibold text-ink-100">{title}</h3>
      <dl className="mt-4 space-y-3">
        {Object.entries(data).map(([key, value]) => (
          <div key={key} className="flex flex-col gap-1 border-b border-white/5 pb-3 last:border-0 last:pb-0">
            <dt className="text-xs uppercase tracking-wide text-ink-700">{key.replace(/_/g, ' ')}</dt>
            <dd className="text-sm text-ink-200">
              {Array.isArray(value) ? value.join(', ') : String(value)}
            </dd>
          </div>
        ))}
      </dl>
    </GlassCard>
  );
}

export function DigitalTwin() {
  const { data: twin, loading, refetch, setData } = useFetch(() => studentService.getDigitalTwin(), []);
  const [regenerating, setRegenerating] = useState(false);
  const notify = useNotification();

  const handleRegenerate = async () => {
    setRegenerating(true);
    try {
      const updated = await studentService.regenerateDigitalTwin();
      setData(updated);
      notify.success('Digital twin regenerated with your latest data.');
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not regenerate your digital twin.');
    } finally {
      setRegenerating(false);
    }
  };

  if (loading) return <PageLoader label="Loading your digital twin…" />;

  return (
    <div className="space-y-6">
      <div className="flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink-100">Your digital twin</h1>
          <p className="mt-1 text-sm text-ink-500">
            An AI-derived model of your behavior, learning pattern, and career fit — built from your profile, skills, and resume.
          </p>
        </div>
        <Button onClick={handleRegenerate} loading={regenerating}>
          <RefreshCcw className="h-4 w-4" /> {twin?.generated ? 'Regenerate' : 'Generate'}
        </Button>
      </div>

      {!twin?.generated ? (
        <GlassCard>
          <EmptyState
            icon={Bot}
            title="Your digital twin hasn't been generated yet"
            description="Complete your profile and upload a resume, then generate your twin to see AI-derived insights here."
            actionLabel="Generate now"
            onAction={handleRegenerate}
          />
        </GlassCard>
      ) : (
        <>
          {twin.confidenceIndex != null && (
            <GlassCard className="flex items-center justify-between">
              <div>
                <p className="text-xs text-ink-500">Confidence index</p>
                <p className="font-display text-3xl font-semibold text-twin-violet">{twin.confidenceIndex}</p>
              </div>
              <p className="max-w-xs text-right text-xs text-ink-500">
                Last generated {twin.lastGeneratedAt ? new Date(twin.lastGeneratedAt).toLocaleString() : '—'}
              </p>
            </GlassCard>
          )}

          <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
            <InsightList title="Behavior profile" data={twin.behaviorProfile} />
            <InsightList title="Learning pattern" data={twin.learningPattern} />
            <InsightList title="Career prediction" data={twin.careerPrediction} />
          </div>
        </>
      )}
    </div>
  );
}

export default DigitalTwin;

import { Link } from 'react-router-dom';
import { Map, CheckCircle2, Circle, BookOpen, Code2, Award, Sparkles as SkillIcon } from 'lucide-react';
import studentService from '../../services/studentService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

const ITEM_ICONS = { COURSE: BookOpen, PROJECT: Code2, CERTIFICATION: Award, SKILL: SkillIcon };
const STATUS_TONES = { ACTIVE: 'cyan', COMPLETED: 'success', ARCHIVED: 'neutral' };

export function Roadmap() {
  const { data: roadmaps, loading, refetch } = useFetch(() => studentService.getRoadmaps(), []);
  const notify = useNotification();

  const handleToggle = async (roadmapId, itemId, completed) => {
    try {
      await studentService.markRoadmapItemComplete(roadmapId, itemId, completed);
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not update this item.');
    }
  };

  if (loading) return <PageLoader label="Loading your roadmaps…" />;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Learning roadmap</h1>
        <p className="mt-1 text-sm text-ink-500">Courses, projects, and certifications to close your skill gaps.</p>
      </div>

      {!roadmaps?.length ? (
        <GlassCard>
          <EmptyState
            icon={Map}
            title="No roadmaps yet"
            description="Run a skill gap analysis first, then generate a roadmap from it."
          />
          <div className="mt-2 text-center">
            <Link to="/student/skill-gap" className="text-sm text-twin-cyan hover:underline">
              Go to skill gap analysis →
            </Link>
          </div>
        </GlassCard>
      ) : (
        <div className="space-y-6">
          {roadmaps.map((r) => (
            <GlassCard key={r.id}>
              <div className="mb-4 flex items-center justify-between">
                <div>
                  <p className="font-display text-lg font-semibold text-ink-100">{r.title}</p>
                  <p className="text-xs text-ink-500">{r.targetRole}</p>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-xs text-ink-500">{r.completedItemCount}/{r.totalItemCount} done</span>
                  <Badge tone={STATUS_TONES[r.status]}>{r.status}</Badge>
                </div>
              </div>

              <div className="h-1.5 w-full overflow-hidden rounded-full bg-white/10">
                <div
                  className="h-full bg-twin-gradient transition-all duration-500"
                  style={{ width: `${r.totalItemCount ? (r.completedItemCount / r.totalItemCount) * 100 : 0}%` }}
                />
              </div>

              <ul className="mt-5 space-y-2">
                {r.items.map((item) => {
                  const Icon = ITEM_ICONS[item.itemType] || SkillIcon;
                  return (
                    <li key={item.id} className="flex items-start gap-3 rounded-lg border border-white/5 p-3">
                      <button onClick={() => handleToggle(r.id, item.id, !item.completed)} className="mt-0.5 flex-shrink-0">
                        {item.completed ? (
                          <CheckCircle2 className="h-5 w-5 text-success" />
                        ) : (
                          <Circle className="h-5 w-5 text-ink-700" />
                        )}
                      </button>
                      <Icon className="mt-0.5 h-4 w-4 flex-shrink-0 text-twin-cyan" />
                      <div className="flex-1">
                        <p className={`text-sm font-medium ${item.completed ? 'text-ink-500 line-through' : 'text-ink-100'}`}>
                          {item.title}
                        </p>
                        {item.description && <p className="mt-0.5 text-xs text-ink-500">{item.description}</p>}
                        {item.resourceUrl && (
                          <a
                            href={item.resourceUrl}
                            target="_blank"
                            rel="noreferrer"
                            className="mt-1 inline-block text-xs text-twin-cyan hover:underline"
                          >
                            View resource →
                          </a>
                        )}
                      </div>
                    </li>
                  );
                })}
              </ul>
            </GlassCard>
          ))}
        </div>
      )}
    </div>
  );
}

export default Roadmap;

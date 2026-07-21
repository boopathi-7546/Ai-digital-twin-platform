import {
  LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid,
  PieChart, Pie, Cell, Legend,
} from 'recharts';
import studentService from '../../services/studentService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

const PIE_COLORS = ['#7C5CFF', '#22D3EE', '#34D399', '#FBBF24'];
const TOOLTIP_STYLE = { background: '#111827', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 };

export function Analytics() {
  const { data, loading, error } = useFetch(() => studentService.getAnalytics(), []);

  if (loading) return <PageLoader label="Crunching your numbers…" />;
  if (error || !data) return <EmptyState title="Couldn't load analytics" description={error} />;

  const skillData = Object.entries(data.skillsByProficiency || {}).map(([name, value]) => ({ name, value }));

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Analytics</h1>
        <p className="mt-1 text-sm text-ink-500">A closer look at your progress over time.</p>
      </div>

      <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
        {[
          ['Resumes uploaded', data.totalResumesUploaded],
          ['Interviews completed', data.totalInterviewsCompleted],
          ['Projects', data.totalProjects],
          ['Certifications', data.totalCertifications],
        ].map(([label, value]) => (
          <GlassCard key={label} className="p-4 text-center">
            <p className="font-display text-2xl font-semibold text-ink-100">{value}</p>
            <p className="mt-1 text-xs text-ink-500">{label}</p>
          </GlassCard>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <GlassCard>
          <h2 className="font-display text-base font-semibold text-ink-100">Resume score over time</h2>
          {data.resumeScoreTrend?.length ? (
            <ResponsiveContainer width="100%" height={260}>
              <LineChart data={data.resumeScoreTrend} margin={{ top: 16, right: 8, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="date" stroke="#8890B5" fontSize={12} />
                <YAxis stroke="#8890B5" fontSize={12} domain={[0, 100]} />
                <Tooltip contentStyle={TOOLTIP_STYLE} />
                <Line type="monotone" dataKey="score" stroke="#7C5CFF" strokeWidth={2} dot={{ r: 3 }} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <EmptyState title="No data yet" description="Upload and analyze a resume to see this chart." />
          )}
        </GlassCard>

        <GlassCard>
          <h2 className="font-display text-base font-semibold text-ink-100">Interview score over time</h2>
          {data.interviewScoreTrend?.length ? (
            <ResponsiveContainer width="100%" height={260}>
              <LineChart data={data.interviewScoreTrend} margin={{ top: 16, right: 8, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="date" stroke="#8890B5" fontSize={12} />
                <YAxis stroke="#8890B5" fontSize={12} domain={[0, 100]} />
                <Tooltip contentStyle={TOOLTIP_STYLE} />
                <Line type="monotone" dataKey="score" stroke="#22D3EE" strokeWidth={2} dot={{ r: 3 }} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <EmptyState title="No data yet" description="Complete a mock interview to see this chart." />
          )}
        </GlassCard>

        <GlassCard>
          <h2 className="font-display text-base font-semibold text-ink-100">Skills by proficiency</h2>
          {skillData.length ? (
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie data={skillData} dataKey="value" nameKey="name" innerRadius={60} outerRadius={90} paddingAngle={3}>
                  {skillData.map((_, i) => <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />)}
                </Pie>
                <Legend />
                <Tooltip contentStyle={TOOLTIP_STYLE} />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <EmptyState title="No skills tracked yet" description="Add skills on your profile page." />
          )}
        </GlassCard>

        <GlassCard>
          <h2 className="font-display text-base font-semibold text-ink-100">Roadmap progress</h2>
          <div className="mt-6 flex flex-col items-center justify-center gap-3">
            <p className="font-display text-4xl font-semibold text-twin-violet">
              {data.totalRoadmapItemCount
                ? Math.round((data.completedRoadmapItemCount / data.totalRoadmapItemCount) * 100)
                : 0}%
            </p>
            <p className="text-sm text-ink-500">
              {data.completedRoadmapItemCount} of {data.totalRoadmapItemCount} items completed across {data.activeRoadmapCount} active roadmap(s)
            </p>
          </div>
        </GlassCard>
      </div>
    </div>
  );
}

export default Analytics;

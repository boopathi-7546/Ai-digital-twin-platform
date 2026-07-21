import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, PieChart, Pie, Cell, Legend } from 'recharts';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

const TOOLTIP_STYLE = { background: '#111827', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 };
const PIE_COLORS = ['#34D399', '#FBBF24', '#8890B5'];

export function Analytics() {
  const { data: stats, loading: statsLoading } = useFetch(() => adminService.getDashboardStats(), []);
  const { data: sessions, loading: sessionsLoading } = useFetch(() => adminService.getAllSessions(), []);

  if (statsLoading || sessionsLoading) return <PageLoader label="Loading platform analytics…" />;
  if (!stats) return <EmptyState title="Couldn't load analytics" />;

  const barData = [
    { name: 'Students', value: stats.totalStudents },
    { name: 'Resumes', value: stats.totalResumesUploaded },
    { name: 'Interviews', value: stats.totalInterviewSessions },
    { name: 'Roadmaps', value: stats.totalRoadmapsGenerated },
    { name: 'Reports', value: stats.totalReportsGenerated },
  ];

  const statusCounts = (sessions || []).reduce((acc, s) => {
    acc[s.status] = (acc[s.status] || 0) + 1;
    return acc;
  }, {});
  const statusData = Object.entries(statusCounts).map(([name, value]) => ({ name, value }));

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Platform analytics</h1>
        <p className="mt-1 text-sm text-ink-500">Aggregate activity across every student on the platform.</p>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <GlassCard>
          <h2 className="font-display text-base font-semibold text-ink-100">Platform totals</h2>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={barData} margin={{ top: 16, right: 8, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
              <XAxis dataKey="name" stroke="#8890B5" fontSize={12} />
              <YAxis stroke="#8890B5" fontSize={12} />
              <Tooltip contentStyle={TOOLTIP_STYLE} />
              <Bar dataKey="value" fill="#7C5CFF" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </GlassCard>

        <GlassCard>
          <h2 className="font-display text-base font-semibold text-ink-100">Interview session status</h2>
          {statusData.length ? (
            <ResponsiveContainer width="100%" height={280}>
              <PieChart>
                <Pie data={statusData} dataKey="value" nameKey="name" innerRadius={60} outerRadius={90} paddingAngle={3}>
                  {statusData.map((_, i) => <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />)}
                </Pie>
                <Legend />
                <Tooltip contentStyle={TOOLTIP_STYLE} />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <EmptyState title="No interview sessions yet" />
          )}
        </GlassCard>
      </div>
    </div>
  );
}

export default Analytics;

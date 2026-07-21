import { Link } from 'react-router-dom';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import { FileText, MessagesSquare, Map, TrendingUp, ArrowRight } from 'lucide-react';
import studentService from '../../services/studentService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useAuth } from '../../hooks/useAuth.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

function StatCard({ label, value, icon: Icon, to }) {
  return (
    <Link to={to}>
      <GlassCard hover className="flex items-center justify-between p-5">
        <div>
          <p className="text-xs text-ink-500">{label}</p>
          <p className="mt-1 font-display text-2xl font-semibold text-ink-100">{value}</p>
        </div>
        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-twin-gradient-soft">
          <Icon className="h-5 w-5 text-twin-cyan" />
        </div>
      </GlassCard>
    </Link>
  );
}

export function Dashboard() {
  const { user } = useAuth();
  const { data: analytics, loading, error } = useFetch(() => studentService.getAnalytics(), []);

  if (loading) return <PageLoader label="Loading your dashboard…" />;

  if (error || !analytics) {
    return (
      <EmptyState
        title="Couldn't load your dashboard"
        description={error || 'Please try refreshing the page.'}
      />
    );
  }

  const firstName = user?.fullName?.split(' ')[0] || 'there';

  return (
    <div className="space-y-8">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Welcome back, {firstName}</h1>
        <p className="mt-1 text-sm text-ink-500">Here's where your readiness stands today.</p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          label="Latest resume score"
          value={analytics.latestResumeScore ? `${analytics.latestResumeScore}` : '—'}
          icon={FileText}
          to="/student/resume"
        />
        <StatCard
          label="Interviews completed"
          value={analytics.totalInterviewsCompleted}
          icon={MessagesSquare}
          to="/student/interview"
        />
        <StatCard
          label="Roadmap progress"
          value={`${analytics.completedRoadmapItemCount}/${analytics.totalRoadmapItemCount || 0}`}
          icon={Map}
          to="/student/roadmap"
        />
        <StatCard
          label="Skills tracked"
          value={analytics.totalSkillsTracked}
          icon={TrendingUp}
          to="/student/profile"
        />
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <GlassCard>
          <h2 className="font-display text-base font-semibold text-ink-100">Resume score trend</h2>
          {analytics.resumeScoreTrend?.length ? (
            <ResponsiveContainer width="100%" height={240}>
              <LineChart data={analytics.resumeScoreTrend} margin={{ top: 16, right: 8, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="date" stroke="#8890B5" fontSize={12} />
                <YAxis stroke="#8890B5" fontSize={12} domain={[0, 100]} />
                <Tooltip contentStyle={{ background: '#111827', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 }} />
                <Line type="monotone" dataKey="score" stroke="#7C5CFF" strokeWidth={2} dot={{ r: 3 }} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <EmptyState
              title="No resume analysis yet"
              description="Upload a resume to see your score trend here."
              actionLabel="Upload resume"
              onAction={() => (window.location.href = '/student/resume')}
            />
          )}
        </GlassCard>

        <GlassCard>
          <h2 className="font-display text-base font-semibold text-ink-100">Interview score trend</h2>
          {analytics.interviewScoreTrend?.length ? (
            <ResponsiveContainer width="100%" height={240}>
              <LineChart data={analytics.interviewScoreTrend} margin={{ top: 16, right: 8, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                <XAxis dataKey="date" stroke="#8890B5" fontSize={12} />
                <YAxis stroke="#8890B5" fontSize={12} domain={[0, 100]} />
                <Tooltip contentStyle={{ background: '#111827', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 }} />
                <Line type="monotone" dataKey="score" stroke="#22D3EE" strokeWidth={2} dot={{ r: 3 }} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <EmptyState
              title="No completed interviews yet"
              description="Start a mock interview to begin tracking your progress."
              actionLabel="Start a mock interview"
              onAction={() => (window.location.href = '/student/interview')}
            />
          )}
        </GlassCard>
      </div>

      <GlassCard className="flex items-center justify-between">
        <div>
          <h2 className="font-display text-base font-semibold text-ink-100">Not sure what to work on?</h2>
          <p className="mt-1 text-sm text-ink-500">Your digital twin can tell you where to focus next.</p>
        </div>
        <Link to="/student/digital-twin" className="btn-primary">
          View digital twin <ArrowRight className="h-4 w-4" />
        </Link>
      </GlassCard>
    </div>
  );
}

export default Dashboard;

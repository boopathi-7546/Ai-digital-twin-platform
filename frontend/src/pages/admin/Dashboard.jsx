import { Users, FileText, MessagesSquare, Wrench, ClipboardList, Map, FileBarChart2, UserCheck } from 'lucide-react';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

function StatCard({ label, value, icon: Icon }) {
  return (
    <GlassCard className="flex items-center justify-between p-5">
      <div>
        <p className="text-xs text-ink-500">{label}</p>
        <p className="mt-1 font-display text-2xl font-semibold text-ink-100">{value}</p>
      </div>
      <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-twin-gradient-soft">
        <Icon className="h-5 w-5 text-twin-cyan" />
      </div>
    </GlassCard>
  );
}

export function Dashboard() {
  const { data: stats, loading, error } = useFetch(() => adminService.getDashboardStats(), []);

  if (loading) return <PageLoader label="Loading platform stats…" />;
  if (error || !stats) return <EmptyState title="Couldn't load dashboard" description={error} />;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Admin dashboard</h1>
        <p className="mt-1 text-sm text-ink-500">Platform-wide activity at a glance.</p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Total students" value={stats.totalStudents} icon={Users} />
        <StatCard label="Active students" value={stats.activeStudents} icon={UserCheck} />
        <StatCard label="Resumes uploaded" value={stats.totalResumesUploaded} icon={FileText} />
        <StatCard label="Interview sessions" value={stats.totalInterviewSessions} icon={MessagesSquare} />
        <StatCard label="Completed interviews" value={stats.completedInterviewSessions} icon={ClipboardList} />
        <StatCard label="Skills in catalog" value={stats.totalSkillsInCatalog} icon={Wrench} />
        <StatCard label="Questions in bank" value={stats.totalQuestionsInBank} icon={ClipboardList} />
        <StatCard label="Roadmaps generated" value={stats.totalRoadmapsGenerated} icon={Map} />
        <StatCard label="Reports generated" value={stats.totalReportsGenerated} icon={FileBarChart2} />
      </div>
    </div>
  );
}

export default Dashboard;

import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Search, CheckCircle2, XCircle } from 'lucide-react';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

export function Students() {
  const { data: students, loading, refetch } = useFetch(() => adminService.getAllStudents(), []);
  const [query, setQuery] = useState('');
  const notify = useNotification();

  const handleToggleActive = async (studentId, currentActive, e) => {
    e.preventDefault();
    e.stopPropagation();
    try {
      await adminService.updateStudentStatus(studentId, !currentActive);
      notify.success(`Student ${!currentActive ? 'activated' : 'deactivated'}.`);
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not update this student.');
    }
  };

  if (loading) return <PageLoader label="Loading students…" />;

  const filtered = (students || []).filter((s) =>
    `${s.fullName} ${s.email} ${s.collegeName}`.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Students</h1>
        <p className="mt-1 text-sm text-ink-500">{students?.length || 0} total registered students</p>
      </div>

      <div className="relative max-w-sm">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-ink-500" />
        <input
          className="input-field pl-9"
          placeholder="Search by name, email, or college…"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>

      {!filtered.length ? (
        <GlassCard><EmptyState title="No students found" /></GlassCard>
      ) : (
        <div className="overflow-x-auto rounded-xl border border-white/10">
          <table className="w-full text-sm">
            <thead className="bg-white/[0.03] text-left text-xs uppercase tracking-wide text-ink-500">
              <tr>
                <th className="px-4 py-3">Name</th>
                <th className="px-4 py-3">College</th>
                <th className="px-4 py-3">Target role</th>
                <th className="px-4 py-3">Resumes</th>
                <th className="px-4 py-3">Interviews</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3" />
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {filtered.map((s) => (
                <tr key={s.studentId} className="hover:bg-white/[0.02]">
                  <td className="px-4 py-3">
                    <Link to={`/admin/students/${s.studentId}`} className="font-medium text-ink-100 hover:text-twin-cyan">
                      {s.fullName}
                    </Link>
                    <p className="text-xs text-ink-500">{s.email}</p>
                  </td>
                  <td className="px-4 py-3 text-ink-300">{s.collegeName || '—'}</td>
                  <td className="px-4 py-3 text-ink-300">{s.targetRole || '—'}</td>
                  <td className="px-4 py-3 text-ink-300">{s.resumeCount}</td>
                  <td className="px-4 py-3 text-ink-300">{s.interviewSessionCount}</td>
                  <td className="px-4 py-3">
                    <Badge tone={s.active ? 'success' : 'danger'}>{s.active ? 'Active' : 'Inactive'}</Badge>
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button
                      onClick={(e) => handleToggleActive(s.studentId, s.active, e)}
                      className="text-ink-500 hover:text-ink-100"
                      title={s.active ? 'Deactivate' : 'Activate'}
                    >
                      {s.active ? <XCircle className="h-4 w-4" /> : <CheckCircle2 className="h-4 w-4" />}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default Students;

import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, CheckCircle2, XCircle } from 'lucide-react';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { Button } from '../../components/common/Button.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';

export function StudentDetail() {
  const { studentId } = useParams();
  const navigate = useNavigate();
  const notify = useNotification();
  const { data: student, loading, refetch } = useFetch(() => adminService.getStudentById(studentId), [studentId]);

  const handleToggleActive = async () => {
    try {
      await adminService.updateStudentStatus(studentId, !student.active);
      notify.success(`Student ${!student.active ? 'activated' : 'deactivated'}.`);
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not update this student.');
    }
  };

  if (loading) return <PageLoader label="Loading student…" />;
  if (!student) return null;

  return (
    <div className="max-w-2xl space-y-6">
      <button onClick={() => navigate('/admin/students')} className="flex items-center gap-1.5 text-sm text-ink-500 hover:text-ink-100">
        <ArrowLeft className="h-4 w-4" /> Back to students
      </button>

      <GlassCard>
        <div className="flex items-start justify-between">
          <div>
            <h1 className="font-display text-xl font-semibold text-ink-100">{student.fullName}</h1>
            <p className="text-sm text-ink-500">{student.email}</p>
          </div>
          <Badge tone={student.active ? 'success' : 'danger'}>{student.active ? 'Active' : 'Inactive'}</Badge>
        </div>

        <dl className="mt-6 grid grid-cols-2 gap-4 text-sm">
          <div>
            <dt className="text-xs text-ink-500">College</dt>
            <dd className="text-ink-100">{student.collegeName || '—'}</dd>
          </div>
          <div>
            <dt className="text-xs text-ink-500">Target role</dt>
            <dd className="text-ink-100">{student.targetRole || '—'}</dd>
          </div>
          <div>
            <dt className="text-xs text-ink-500">Email verified</dt>
            <dd className="text-ink-100">{student.emailVerified ? 'Yes' : 'No'}</dd>
          </div>
          <div>
            <dt className="text-xs text-ink-500">Last login</dt>
            <dd className="text-ink-100">{student.lastLoginAt ? new Date(student.lastLoginAt).toLocaleString() : 'Never'}</dd>
          </div>
          <div>
            <dt className="text-xs text-ink-500">Resumes uploaded</dt>
            <dd className="text-ink-100">{student.resumeCount}</dd>
          </div>
          <div>
            <dt className="text-xs text-ink-500">Interview sessions</dt>
            <dd className="text-ink-100">{student.interviewSessionCount}</dd>
          </div>
          <div>
            <dt className="text-xs text-ink-500">Joined</dt>
            <dd className="text-ink-100">{new Date(student.createdAt).toLocaleDateString()}</dd>
          </div>
        </dl>

        <div className="mt-6">
          <Button variant={student.active ? 'danger' : 'primary'} size="sm" onClick={handleToggleActive}>
            {student.active ? <XCircle className="h-4 w-4" /> : <CheckCircle2 className="h-4 w-4" />}
            {student.active ? 'Deactivate account' : 'Activate account'}
          </Button>
        </div>
      </GlassCard>
    </div>
  );
}

export default StudentDetail;

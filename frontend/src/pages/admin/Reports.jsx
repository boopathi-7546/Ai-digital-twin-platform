import { useForm } from 'react-hook-form';
import { FileBarChart2, Download, Sparkles } from 'lucide-react';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

const TYPE_TONES = { RESUME: 'cyan', INTERVIEW: 'violet', PROGRESS: 'success' };

export function Reports() {
  const { data: reports, loading, refetch } = useFetch(() => adminService.getAllReports(), []);
  const { register, handleSubmit, watch, formState: { isSubmitting } } = useForm({
    defaultValues: { reportType: 'PROGRESS', studentId: '' },
  });
  const notify = useNotification();
  const reportType = watch('reportType');

  const onSubmit = async (formData) => {
    try {
      await adminService.generateReport({
        reportType: formData.reportType,
        studentId: formData.studentId ? Number(formData.studentId) : null,
      });
      notify.success('Report generated.');
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not generate this report.');
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Reports</h1>
        <p className="mt-1 text-sm text-ink-500">Generate and download resume, interview, and progress reports.</p>
      </div>

      <GlassCard>
        <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4 sm:flex-row sm:items-end">
          <div>
            <label className="label-field">Report type</label>
            <select className="input-field" {...register('reportType')}>
              <option value="PROGRESS">Progress</option>
              <option value="RESUME">Resume</option>
              <option value="INTERVIEW">Interview</option>
            </select>
          </div>
          {reportType !== undefined && (
            <div className="flex-1">
              <label className="label-field">Student ID (leave blank for platform-wide)</label>
              <input type="number" className="input-field" {...register('studentId')} />
            </div>
          )}
          <Button type="submit" loading={isSubmitting}>
            <Sparkles className="h-4 w-4" /> Generate report
          </Button>
        </form>
      </GlassCard>

      {loading ? (
        <PageLoader label="Loading reports…" />
      ) : !reports?.length ? (
        <GlassCard><EmptyState icon={FileBarChart2} title="No reports generated yet" /></GlassCard>
      ) : (
        <div className="space-y-2">
          {reports.map((r) => (
            <GlassCard key={r.id} className="flex items-center justify-between p-4">
              <div className="flex items-center gap-3">
                <Badge tone={TYPE_TONES[r.reportType]}>{r.reportType}</Badge>
                <div>
                  <p className="text-sm text-ink-100">{r.studentName || 'Platform-wide'}</p>
                  <p className="text-xs text-ink-500">{new Date(r.generatedAt).toLocaleString()}</p>
                </div>
              </div>
              <a href={adminService.downloadReportUrl(r.id)} target="_blank" rel="noreferrer">
                <Button size="sm" variant="secondary"><Download className="h-3.5 w-3.5" /> Download</Button>
              </a>
            </GlassCard>
          ))}
        </div>
      )}
    </div>
  );
}

export default Reports;

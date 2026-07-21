import { useRef, useState } from 'react';
import { UploadCloud, FileText, Download, Trash2, Sparkles, CheckCircle2, AlertTriangle } from 'lucide-react';
import resumeService from '../../services/resumeService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

export function Resume() {
  const { data: resumes, loading, refetch } = useFetch(() => resumeService.getMyResumes(), []);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [selectedResumeId, setSelectedResumeId] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [analyzing, setAnalyzing] = useState(false);
  const fileInputRef = useRef(null);
  const notify = useNotification();

  const handleFileChange = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    setUploadProgress(0);
    try {
      await resumeService.uploadResume(file, setUploadProgress);
      notify.success('Resume uploaded.');
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not upload your resume.');
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  };

  const handleAnalyze = async (resumeId) => {
    setSelectedResumeId(resumeId);
    setAnalyzing(true);
    setAnalysis(null);
    try {
      const result = await resumeService.analyzeResume(resumeId);
      setAnalysis(result);
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not analyze this resume.');
    } finally {
      setAnalyzing(false);
    }
  };

  const handleDelete = async (resumeId) => {
    try {
      await resumeService.deleteResume(resumeId);
      notify.success('Resume deleted.');
      if (selectedResumeId === resumeId) {
        setSelectedResumeId(null);
        setAnalysis(null);
      }
      refetch();
    } catch (err) {
      notify.error(err?.response?.data?.message || 'Could not delete this resume.');
    }
  };

  if (loading) return <PageLoader label="Loading your resumes…" />;

  return (
    <div className="space-y-6">
      <div className="flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink-100">Resume</h1>
          <p className="mt-1 text-sm text-ink-500">Upload a PDF or DOCX and let AI score and improve it.</p>
        </div>
        <div>
          <input ref={fileInputRef} type="file" accept=".pdf,.doc,.docx" className="hidden" onChange={handleFileChange} />
          <Button onClick={() => fileInputRef.current?.click()} loading={uploading}>
            <UploadCloud className="h-4 w-4" /> {uploading ? `Uploading ${uploadProgress}%` : 'Upload resume'}
          </Button>
        </div>
      </div>

      {!resumes?.length ? (
        <GlassCard>
          <EmptyState
            icon={FileText}
            title="No resumes uploaded yet"
            description="Upload your first resume to get an AI-powered score and suggestions."
          />
        </GlassCard>
      ) : (
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <div className="space-y-3">
            {resumes.map((r) => (
              <GlassCard key={r.resumeId} className={`p-5 ${selectedResumeId === r.resumeId ? 'border-twin-violet/40' : ''}`}>
                <div className="flex items-start justify-between">
                  <div className="flex items-start gap-3">
                    <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-twin-gradient-soft">
                      <FileText className="h-5 w-5 text-twin-cyan" />
                    </div>
                    <div>
                      <p className="font-medium text-ink-100">{r.fileName}</p>
                      <p className="text-xs text-ink-500">
                        {new Date(r.uploadedAt).toLocaleDateString()} · {(r.fileSizeBytes / 1024).toFixed(0)} KB
                      </p>
                    </div>
                  </div>
                  <button onClick={() => handleDelete(r.resumeId)} className="text-ink-500 hover:text-danger" aria-label="Delete">
                    <Trash2 className="h-4 w-4" />
                  </button>
                </div>

                <div className="mt-4 flex gap-2">
                  <Button size="sm" onClick={() => handleAnalyze(r.resumeId)} loading={analyzing && selectedResumeId === r.resumeId}>
                    <Sparkles className="h-3.5 w-3.5" /> Analyze
                  </Button>
                  <a href={resumeService.downloadResumeUrl(r.resumeId)} target="_blank" rel="noreferrer">
                    <Button size="sm" variant="secondary"><Download className="h-3.5 w-3.5" /> Download</Button>
                  </a>
                </div>
              </GlassCard>
            ))}
          </div>

          <GlassCard>
            <h2 className="font-display text-base font-semibold text-ink-100">AI Analysis</h2>
            {analyzing && <PageLoader label="Analyzing your resume…" />}
            {!analyzing && !analysis && (
              <p className="mt-4 text-sm text-ink-500">Select a resume and click Analyze to see its score and suggestions here.</p>
            )}
            {!analyzing && analysis && (
              <div className="mt-4 space-y-5">
                <div className="grid grid-cols-2 gap-4">
                  <div className="rounded-lg border border-white/10 bg-white/[0.03] p-4 text-center">
                    <p className="text-xs text-ink-500">Overall score</p>
                    <p className="font-display text-3xl font-semibold text-twin-violet">{analysis.overallScore ?? '—'}</p>
                  </div>
                  <div className="rounded-lg border border-white/10 bg-white/[0.03] p-4 text-center">
                    <p className="text-xs text-ink-500">ATS score</p>
                    <p className="font-display text-3xl font-semibold text-twin-cyan">{analysis.atsScore ?? '—'}</p>
                  </div>
                </div>

                {analysis.extractedSkills?.length > 0 && (
                  <div>
                    <p className="mb-2 text-sm font-medium text-ink-300">Extracted skills</p>
                    <div className="flex flex-wrap gap-2">
                      {analysis.extractedSkills.map((s) => <Badge key={s} tone="cyan">{s}</Badge>)}
                    </div>
                  </div>
                )}

                {analysis.strengths?.length > 0 && (
                  <div>
                    <p className="mb-2 flex items-center gap-1.5 text-sm font-medium text-success">
                      <CheckCircle2 className="h-4 w-4" /> Strengths
                    </p>
                    <ul className="space-y-1 text-sm text-ink-300">
                      {analysis.strengths.map((s, i) => <li key={i}>• {s}</li>)}
                    </ul>
                  </div>
                )}

                {analysis.weaknesses?.length > 0 && (
                  <div>
                    <p className="mb-2 flex items-center gap-1.5 text-sm font-medium text-warning">
                      <AlertTriangle className="h-4 w-4" /> Areas to improve
                    </p>
                    <ul className="space-y-1 text-sm text-ink-300">
                      {analysis.weaknesses.map((s, i) => <li key={i}>• {s}</li>)}
                    </ul>
                  </div>
                )}

                {analysis.suggestions?.length > 0 && (
                  <div>
                    <p className="mb-2 text-sm font-medium text-ink-300">Suggestions</p>
                    <ul className="space-y-1 text-sm text-ink-300">
                      {analysis.suggestions.map((s, i) => <li key={i}>• {s}</li>)}
                    </ul>
                  </div>
                )}

                {analysis.predictedRoles?.length > 0 && (
                  <div>
                    <p className="mb-2 text-sm font-medium text-ink-300">Predicted best-fit roles</p>
                    <div className="flex flex-wrap gap-2">
                      {analysis.predictedRoles.map((r) => <Badge key={r} tone="violet">{r}</Badge>)}
                    </div>
                  </div>
                )}
              </div>
            )}
          </GlassCard>
        </div>
      )}
    </div>
  );
}

export default Resume;

import { Clock, HardDrive, Globe, Bot } from 'lucide-react';
import adminService from '../../services/adminService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';

function msToReadable(ms) {
  const hours = ms / (1000 * 60 * 60);
  if (hours >= 24) return `${(hours / 24).toFixed(1)} days`;
  if (hours >= 1) return `${hours.toFixed(1)} hours`;
  return `${(ms / 1000 / 60).toFixed(0)} minutes`;
}

export function Settings() {
  const { data: settings, loading } = useFetch(() => adminService.getSettings(), []);

  if (loading) return <PageLoader label="Loading system settings…" />;
  if (!settings) return null;

  const geminiConfigured = settings.geminiApiConfigured === 'true' || settings.geminiApiConfigured === true;

  return (
    <div className="max-w-2xl space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">System settings</h1>
        <p className="mt-1 text-sm text-ink-500">
          Read-only snapshot of platform configuration. Changing these values requires a redeploy.
        </p>
      </div>

      <GlassCard>
        <div className="flex items-center gap-3 border-b border-white/5 pb-4">
          <Clock className="h-5 w-5 text-twin-cyan" />
          <div>
            <p className="text-sm font-medium text-ink-100">Session tokens</p>
            <p className="text-xs text-ink-500">
              Access token: {msToReadable(settings.accessTokenExpirationMs)} · Refresh token: {msToReadable(settings.refreshTokenExpirationMs)}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3 border-b border-white/5 py-4">
          <HardDrive className="h-5 w-5 text-twin-cyan" />
          <div>
            <p className="text-sm font-medium text-ink-100">File storage</p>
            <p className="text-xs text-ink-500">
              Max resume size: {settings.maxResumeSizeMb} MB · Directory: {settings.uploadDirectory}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3 border-b border-white/5 py-4">
          <Globe className="h-5 w-5 text-twin-cyan" />
          <div>
            <p className="text-sm font-medium text-ink-100">CORS allowed origins</p>
            <p className="text-xs text-ink-500">{settings.corsAllowedOrigins}</p>
          </div>
        </div>

        <div className="flex items-center gap-3 pt-4">
          <Bot className="h-5 w-5 text-twin-cyan" />
          <div className="flex items-center gap-2">
            <p className="text-sm font-medium text-ink-100">Gemini API</p>
            <Badge tone={geminiConfigured ? 'success' : 'danger'}>
              {geminiConfigured ? 'Configured' : 'Not configured'}
            </Badge>
          </div>
        </div>
      </GlassCard>
    </div>
  );
}

export default Settings;

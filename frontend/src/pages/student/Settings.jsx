import { Moon, Sun, LogOut, Mail, KeyRound } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth.js';
import { useTheme } from '../../hooks/useTheme.js';
import { useNotification } from '../../hooks/useNotification.js';
import authService from '../../services/authService.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';

export function Settings() {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const notify = useNotification();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handlePasswordReset = async () => {
    try {
      await authService.forgotPassword(user.email);
      notify.success('Password reset link sent to your email.');
    } catch {
      notify.error('Could not send the reset link. Please try again.');
    }
  };

  return (
    <div className="max-w-2xl space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink-100">Settings</h1>
        <p className="mt-1 text-sm text-ink-500">Manage your account and preferences.</p>
      </div>

      <GlassCard>
        <h2 className="font-display text-base font-semibold text-ink-100">Account</h2>
        <div className="mt-4 space-y-3 text-sm">
          <div className="flex items-center justify-between border-b border-white/5 pb-3">
            <span className="text-ink-500">Full name</span>
            <span className="text-ink-100">{user?.fullName}</span>
          </div>
          <div className="flex items-center justify-between">
            <span className="flex items-center gap-1.5 text-ink-500"><Mail className="h-4 w-4" /> Email</span>
            <span className="text-ink-100">{user?.email}</span>
          </div>
        </div>
      </GlassCard>

      <GlassCard>
        <h2 className="font-display text-base font-semibold text-ink-100">Appearance</h2>
        <div className="mt-4 flex items-center justify-between">
          <span className="text-sm text-ink-500">Theme</span>
          <Button variant="secondary" size="sm" onClick={toggleTheme}>
            {theme === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
            {theme === 'dark' ? 'Switch to light' : 'Switch to dark'}
          </Button>
        </div>
      </GlassCard>

      <GlassCard>
        <h2 className="font-display text-base font-semibold text-ink-100">Security</h2>
        <div className="mt-4 flex items-center justify-between">
          <span className="text-sm text-ink-500">Password</span>
          <Button variant="secondary" size="sm" onClick={handlePasswordReset}>
            <KeyRound className="h-4 w-4" /> Send reset link
          </Button>
        </div>
      </GlassCard>

      <GlassCard>
        <h2 className="font-display text-base font-semibold text-danger">Danger zone</h2>
        <div className="mt-4 flex items-center justify-between">
          <span className="text-sm text-ink-500">Sign out of your account</span>
          <Button variant="danger" size="sm" onClick={handleLogout}>
            <LogOut className="h-4 w-4" /> Log out
          </Button>
        </div>
      </GlassCard>
    </div>
  );
}

export default Settings;

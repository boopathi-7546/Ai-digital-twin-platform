import { useState, useRef, useEffect } from 'react';
import { Menu, Moon, Sun, ChevronDown, LogOut, Settings as SettingsIcon } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth.js';
import { useTheme } from '../../hooks/useTheme.js';
import { NotificationBell } from './NotificationBell.jsx';

export function Topbar({ onMenuClick }) {
  const { user, logout, isAdmin } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    const onClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) setMenuOpen(false);
    };
    document.addEventListener('mousedown', onClickOutside);
    return () => document.removeEventListener('mousedown', onClickOutside);
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const initials = (user?.fullName || 'U')
    .split(' ')
    .map((p) => p[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();

  return (
    <header className="sticky top-0 z-20 flex h-16 items-center justify-between border-b border-white/10 bg-navy-950/70 px-4 backdrop-blur-glass lg:px-8">
      <button
        onClick={onMenuClick}
        className="rounded-lg p-2 text-ink-300 hover:bg-white/[0.06] hover:text-ink-100 lg:hidden"
        aria-label="Open menu"
      >
        <Menu className="h-5 w-5" />
      </button>

      <div className="hidden lg:block" />

      <div className="flex items-center gap-2">
        <button
          onClick={toggleTheme}
          className="rounded-lg p-2 text-ink-300 transition-colors hover:bg-white/[0.06] hover:text-ink-100"
          aria-label="Toggle theme"
        >
          {theme === 'dark' ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
        </button>

        <NotificationBell />

        <div className="relative" ref={menuRef}>
          <button
            onClick={() => setMenuOpen((p) => !p)}
            className="flex items-center gap-2 rounded-lg p-1.5 pr-2 transition-colors hover:bg-white/[0.06]"
          >
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-twin-gradient text-xs font-semibold text-navy-950">
              {initials}
            </div>
            <span className="hidden text-sm text-ink-100 sm:block">{user?.fullName}</span>
            <ChevronDown className="h-4 w-4 text-ink-500" />
          </button>

          {menuOpen && (
            <div className="glass-card absolute right-0 top-12 w-48 p-1.5">
              <p className="truncate px-3 py-2 text-xs text-ink-500">{user?.email}</p>
              <Link
                to={isAdmin ? '/admin/settings' : '/student/settings'}
                onClick={() => setMenuOpen(false)}
                className="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-ink-100 hover:bg-white/[0.06]"
              >
                <SettingsIcon className="h-4 w-4" /> Settings
              </Link>
              <button
                onClick={handleLogout}
                className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm text-danger hover:bg-white/[0.06]"
              >
                <LogOut className="h-4 w-4" /> Log out
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

export default Topbar;

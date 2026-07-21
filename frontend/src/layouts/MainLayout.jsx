import { Link, Outlet } from 'react-router-dom';
import { Sparkles } from 'lucide-react';
import { useAuth } from '../hooks/useAuth.js';

/**
 * Public-facing shell used for the landing page and any other
 * unauthenticated marketing/info routes. Simple header + footer,
 * content in between via Outlet.
 */
export function MainLayout() {
  const { isAuthenticated, isAdmin } = useAuth();
  const dashboardPath = isAdmin ? '/admin/dashboard' : '/student/dashboard';

  return (
    <div className="flex min-h-screen flex-col bg-navy-950">
      <header className="sticky top-0 z-30 border-b border-white/10 bg-navy-950/80 backdrop-blur-glass">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
          <Link to="/" className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-twin-gradient">
              <Sparkles className="h-4 w-4 text-navy-950" />
            </div>
            <span className="font-display text-base font-semibold text-ink-100">Digital Twin</span>
          </Link>

          <nav className="flex items-center gap-3">
            {isAuthenticated ? (
              <Link to={dashboardPath} className="btn-primary">Go to Dashboard</Link>
            ) : (
              <>
                <Link to="/login" className="btn-secondary">Log in</Link>
                <Link to="/register" className="btn-primary">Get started</Link>
              </>
            )}
          </nav>
        </div>
      </header>

      <main className="flex-1">
        <Outlet />
      </main>

      <footer className="border-t border-white/10 px-6 py-8 text-center text-sm text-ink-700">
        &copy; {new Date().getFullYear()} AI-Powered Digital Twin & Interview Readiness Platform. Built for students, by design.
      </footer>
    </div>
  );
}

export default MainLayout;

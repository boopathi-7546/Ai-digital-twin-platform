import { Outlet, Link } from 'react-router-dom';
import { Sparkles } from 'lucide-react';

/**
 * Split layout for unauthenticated pages: brand/pitch panel on the
 * left (hidden on small screens), the actual form on the right. Kept
 * deliberately calm — the twin-gradient signature appears once, as
 * an accent, not as decoration competing with the form.
 */
export function AuthLayout() {
  return (
    <div className="flex min-h-screen bg-navy-950">
      <div className="relative hidden w-1/2 flex-col justify-between overflow-hidden bg-navy-900 p-12 lg:flex">
        <div className="absolute inset-0 bg-navy-radial opacity-80" />
        <div className="relative z-10 flex items-center gap-2">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-twin-gradient">
            <Sparkles className="h-5 w-5 text-navy-950" />
          </div>
          <span className="font-display text-lg font-semibold text-ink-100">Digital Twin</span>
        </div>

        <div className="relative z-10 max-w-md">
          <h1 className="font-display text-4xl font-semibold leading-tight text-ink-100">
            Your career readiness,
            <br />
            <span className="bg-twin-gradient bg-clip-text text-transparent">modeled and mentored.</span>
          </h1>
          <p className="mt-4 text-ink-300">
            Upload your resume, meet your digital twin, and walk into every interview already having practiced it.
          </p>
        </div>

        <p className="relative z-10 text-sm text-ink-700">
          &copy; {new Date().getFullYear()} AI-Powered Digital Twin & Interview Readiness Platform
        </p>
      </div>

      <div className="flex w-full flex-col items-center justify-center px-6 py-12 lg:w-1/2">
        <div className="mb-8 flex items-center gap-2 lg:hidden">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-twin-gradient">
            <Sparkles className="h-4 w-4 text-navy-950" />
          </div>
          <span className="font-display text-base font-semibold text-ink-100">Digital Twin</span>
        </div>

        <div className="w-full max-w-sm">
          <Outlet />
        </div>

        <p className="mt-8 text-xs text-ink-700">
          Need help? <Link to="/" className="text-twin-cyan hover:underline">Contact support</Link>
        </p>
      </div>
    </div>
  );
}

export default AuthLayout;

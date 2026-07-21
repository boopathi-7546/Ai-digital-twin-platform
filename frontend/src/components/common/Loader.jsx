import { Loader2 } from 'lucide-react';
import clsx from 'clsx';

/**
 * Inline spinner for use inside cards/buttons.
 */
export function Loader({ size = 20, className }) {
  return <Loader2 className={clsx('animate-spin text-twin-violet', className)} size={size} />;
}

/**
 * Full-page loading screen shown while a route's primary data loads.
 */
export function PageLoader({ label = 'Loading…' }) {
  return (
    <div className="flex h-full min-h-[40vh] w-full flex-col items-center justify-center gap-3 text-ink-500">
      <Loader2 className="h-8 w-8 animate-spin text-twin-violet" />
      <p className="text-sm">{label}</p>
    </div>
  );
}

export default Loader;

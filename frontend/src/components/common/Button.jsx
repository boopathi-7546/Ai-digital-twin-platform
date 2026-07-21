import clsx from 'clsx';
import { Loader2 } from 'lucide-react';

/**
 * Shared button. variant="primary" uses the twin-gradient signature;
 * variant="secondary"/"ghost"/"danger" cover the rest of the app's needs.
 */
export function Button({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  className,
  type = 'button',
  ...props
}) {
  const base = 'inline-flex items-center justify-center gap-2 rounded-lg font-medium transition-all duration-200 disabled:opacity-50 disabled:pointer-events-none';

  const variants = {
    primary: 'bg-twin-gradient text-navy-950 hover:scale-[1.02] active:scale-[0.98]',
    secondary: 'border border-white/15 bg-white/[0.04] text-ink-100 hover:bg-white/[0.08]',
    ghost: 'text-ink-300 hover:text-ink-100 hover:bg-white/[0.06]',
    danger: 'bg-danger/90 text-navy-950 hover:bg-danger',
  };

  const sizes = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2.5 text-sm',
    lg: 'px-6 py-3 text-base',
  };

  return (
    <button
      type={type}
      disabled={disabled || loading}
      className={clsx(base, variants[variant], sizes[size], className)}
      {...props}
    >
      {loading && <Loader2 className="h-4 w-4 animate-spin" />}
      {children}
    </button>
  );
}

export default Button;

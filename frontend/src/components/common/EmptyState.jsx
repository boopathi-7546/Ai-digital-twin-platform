import { Inbox } from 'lucide-react';
import { Button } from './Button.jsx';

/**
 * Used whenever a list/table has no data yet (no resumes uploaded, no
 * interviews taken, etc.). Treated as an invitation to act, per the
 * interface's own voice — not just a blank space.
 */
export function EmptyState({ icon: Icon = Inbox, title, description, actionLabel, onAction }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
      <div className="flex h-14 w-14 items-center justify-center rounded-full bg-white/[0.06]">
        <Icon className="h-6 w-6 text-ink-500" />
      </div>
      <h3 className="text-base font-medium text-ink-100">{title}</h3>
      {description && <p className="max-w-sm text-sm text-ink-500">{description}</p>}
      {actionLabel && onAction && (
        <Button variant="secondary" size="sm" onClick={onAction} className="mt-2">
          {actionLabel}
        </Button>
      )}
    </div>
  );
}

export default EmptyState;

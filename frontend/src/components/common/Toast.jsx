import { motion } from 'framer-motion';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';
import clsx from 'clsx';

const ICONS = {
  success: CheckCircle2,
  error: AlertCircle,
  info: Info,
};

const TONE_BORDER = {
  success: 'border-success/40',
  error: 'border-danger/40',
  info: 'border-twin-cyan/40',
};

const TONE_ICON_COLOR = {
  success: 'text-success',
  error: 'text-danger',
  info: 'text-twin-cyan',
};

/**
 * A single transient toast rendered by NotificationContext. Every
 * toast names what happened plainly (no vague "Something happened")
 * so it reads as the interface's own voice, not filler.
 */
export function Toast({ message, type = 'info', onClose }) {
  const Icon = ICONS[type] || Info;

  return (
    <motion.div
      role="status"
      initial={{ opacity: 0, y: 12, scale: 0.96 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      exit={{ opacity: 0, y: 12, scale: 0.96 }}
      className={clsx(
        'glass-card flex w-80 items-start gap-3 border p-4 shadow-glass',
        TONE_BORDER[type] || TONE_BORDER.info
      )}
    >
      <Icon className={clsx('mt-0.5 h-5 w-5 flex-shrink-0', TONE_ICON_COLOR[type] || TONE_ICON_COLOR.info)} />
      <p className="flex-1 text-sm text-ink-100">{message}</p>
      <button
        onClick={onClose}
        className="text-ink-500 transition-colors hover:text-ink-100"
        aria-label="Dismiss notification"
      >
        <X className="h-4 w-4" />
      </button>
    </motion.div>
  );
}

export default Toast;

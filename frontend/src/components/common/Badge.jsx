import clsx from 'clsx';

const TONE_STYLES = {
  neutral: 'bg-white/10 text-ink-300',
  violet: 'bg-twin-violet/15 text-twin-violet',
  cyan: 'bg-twin-cyan/15 text-twin-cyan',
  success: 'bg-success/15 text-success',
  warning: 'bg-warning/15 text-warning',
  danger: 'bg-danger/15 text-danger',
};

/**
 * Small pill used for statuses (IN_PROGRESS/COMPLETED), proficiency
 * levels, difficulty tags, and skill chips.
 */
export function Badge({ children, tone = 'neutral', className, ...props }) {
  return (
    <span className={clsx('badge', TONE_STYLES[tone] || TONE_STYLES.neutral, className)} {...props}>
      {children}
    </span>
  );
}

export default Badge;

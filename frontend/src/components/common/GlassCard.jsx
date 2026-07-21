import clsx from 'clsx';

/**
 * The platform's signature surface: translucent, blurred, subtly
 * bordered. Every dashboard widget, form, and panel sits on one of
 * these rather than a flat white/gray card.
 */
export function GlassCard({ children, className, hover = false, as: Component = 'div', ...props }) {
  return (
    <Component className={clsx(hover ? 'glass-card-hover' : 'glass-card', 'p-6', className)} {...props}>
      {children}
    </Component>
  );
}

export default GlassCard;

import { useEffect, useRef, useState } from 'react';
import { Bell } from 'lucide-react';
import { AnimatePresence, motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import clsx from 'clsx';
import notificationService from '../../services/notificationService.js';

/**
 * Shows the authenticated user's unread count and a quick preview of
 * their most recent notifications (durable, backend-persisted ones —
 * distinct from the transient toasts in NotificationContext).
 */
export function NotificationBell() {
  const [open, setOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const containerRef = useRef(null);

  const load = async () => {
    try {
      const [list, countResponse] = await Promise.all([
        notificationService.getMyNotifications(),
        notificationService.getUnreadCount(),
      ]);
      setNotifications(list.slice(0, 6));
      setUnreadCount(countResponse.unreadCount || 0);
    } catch {
      // Silently ignore — the bell just won't update this cycle.
    }
  };

  useEffect(() => {
    load();
    const interval = setInterval(load, 60000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const onClickOutside = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', onClickOutside);
    return () => document.removeEventListener('mousedown', onClickOutside);
  }, []);

  const handleOpen = async () => {
    setOpen((prev) => !prev);
  };

  return (
    <div className="relative" ref={containerRef}>
      <button
        onClick={handleOpen}
        className="relative rounded-lg p-2 text-ink-300 transition-colors hover:bg-white/[0.06] hover:text-ink-100"
        aria-label="Notifications"
      >
        <Bell className="h-5 w-5" />
        {unreadCount > 0 && (
          <span className="absolute right-1.5 top-1.5 flex h-2 w-2 rounded-full bg-twin-cyan" />
        )}
      </button>

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            transition={{ duration: 0.15 }}
            className="glass-card absolute right-0 top-12 w-80 p-2"
          >
            <div className="flex items-center justify-between px-2 py-1.5">
              <span className="text-sm font-medium text-ink-100">Notifications</span>
              {unreadCount > 0 && <span className="text-xs text-twin-cyan">{unreadCount} unread</span>}
            </div>

            {notifications.length === 0 ? (
              <p className="px-2 py-6 text-center text-sm text-ink-500">You're all caught up.</p>
            ) : (
              <ul className="max-h-80 overflow-y-auto">
                {notifications.map((n) => (
                  <li
                    key={n.id}
                    className={clsx(
                      'rounded-lg px-2 py-2 text-sm transition-colors hover:bg-white/[0.05]',
                      !n.read && 'bg-white/[0.03]'
                    )}
                  >
                    <p className="font-medium text-ink-100">{n.title}</p>
                    <p className="mt-0.5 line-clamp-2 text-xs text-ink-500">{n.message}</p>
                  </li>
                ))}
              </ul>
            )}

            <Link
              to="/student/notifications"
              onClick={() => setOpen(false)}
              className="mt-1 block rounded-lg px-2 py-2 text-center text-xs font-medium text-twin-cyan hover:bg-white/[0.05]"
            >
              View all
            </Link>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

export default NotificationBell;

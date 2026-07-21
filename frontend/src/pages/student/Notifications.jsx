import { Bell, CheckCheck, Trash2 } from 'lucide-react';
import clsx from 'clsx';
import notificationService from '../../services/notificationService.js';
import { useFetch } from '../../hooks/useFetch.js';
import { useNotification } from '../../hooks/useNotification.js';
import { GlassCard } from '../../components/common/GlassCard.jsx';
import { Button } from '../../components/common/Button.jsx';
import { Badge } from '../../components/common/Badge.jsx';
import { PageLoader } from '../../components/common/Loader.jsx';
import { EmptyState } from '../../components/common/EmptyState.jsx';

const TYPE_TONES = { INFO: 'cyan', SUCCESS: 'success', WARNING: 'warning', ALERT: 'danger' };

export function Notifications() {
  const { data: notifications, loading, refetch } = useFetch(() => notificationService.getMyNotifications(), []);
  const notify = useNotification();

  const handleMarkRead = async (id) => {
    try {
      await notificationService.markAsRead(id);
      refetch();
    } catch {
      notify.error('Could not mark this notification as read.');
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationService.markAllAsRead();
      notify.success('All notifications marked as read.');
      refetch();
    } catch {
      notify.error('Could not mark all notifications as read.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await notificationService.delete(id);
      refetch();
    } catch {
      notify.error('Could not delete this notification.');
    }
  };

  if (loading) return <PageLoader label="Loading your notifications…" />;

  const unreadCount = notifications?.filter((n) => !n.read).length || 0;

  return (
    <div className="space-y-6">
      <div className="flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink-100">Notifications</h1>
          <p className="mt-1 text-sm text-ink-500">{unreadCount} unread</p>
        </div>
        {unreadCount > 0 && (
          <Button size="sm" variant="secondary" onClick={handleMarkAllRead}>
            <CheckCheck className="h-4 w-4" /> Mark all as read
          </Button>
        )}
      </div>

      {!notifications?.length ? (
        <GlassCard>
          <EmptyState icon={Bell} title="You're all caught up" description="New notifications will show up here." />
        </GlassCard>
      ) : (
        <div className="space-y-2">
          {notifications.map((n) => (
            <GlassCard
              key={n.id}
              className={clsx('flex items-start justify-between p-4', !n.read && 'border-twin-violet/30')}
            >
              <div className="flex items-start gap-3">
                <Badge tone={TYPE_TONES[n.type] || 'neutral'}>{n.type}</Badge>
                <div>
                  <p className="font-medium text-ink-100">{n.title}</p>
                  <p className="mt-0.5 text-sm text-ink-500">{n.message}</p>
                  <p className="mt-1 text-xs text-ink-700">{new Date(n.createdAt).toLocaleString()}</p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                {!n.read && (
                  <button onClick={() => handleMarkRead(n.id)} className="text-xs text-twin-cyan hover:underline">
                    Mark read
                  </button>
                )}
                <button onClick={() => handleDelete(n.id)} className="text-ink-500 hover:text-danger" aria-label="Delete">
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>
            </GlassCard>
          ))}
        </div>
      )}
    </div>
  );
}

export default Notifications;

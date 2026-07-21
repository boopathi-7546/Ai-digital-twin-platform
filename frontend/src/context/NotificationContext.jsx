import { createContext, useCallback, useMemo, useState } from 'react';
import { AnimatePresence } from 'framer-motion';
import Toast from '../components/common/Toast.jsx';

export const NotificationContext = createContext(null);

let idCounter = 0;

/**
 * Lightweight toast/snackbar system for transient UI feedback ("Saved",
 * "Something went wrong"). Distinct from the backend's persisted
 * Notification entity (see NotificationBell), which represents
 * durable, cross-session notifications.
 */
export function NotificationProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const dismiss = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const notify = useCallback((message, type = 'info', durationMs = 4000) => {
    const id = ++idCounter;
    setToasts((prev) => [...prev, { id, message, type }]);
    if (durationMs > 0) {
      setTimeout(() => dismiss(id), durationMs);
    }
    return id;
  }, [dismiss]);

  const value = useMemo(() => ({
    notify,
    success: (msg, ms) => notify(msg, 'success', ms),
    error: (msg, ms) => notify(msg, 'error', ms),
    info: (msg, ms) => notify(msg, 'info', ms),
    dismiss,
  }), [notify, dismiss]);

  return (
    <NotificationContext.Provider value={value}>
      {children}
      <div className="fixed bottom-6 right-6 z-50 flex flex-col gap-3">
        <AnimatePresence>
          {toasts.map((toast) => (
            <Toast key={toast.id} {...toast} onClose={() => dismiss(toast.id)} />
          ))}
        </AnimatePresence>
      </div>
    </NotificationContext.Provider>
  );
}

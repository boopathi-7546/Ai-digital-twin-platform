import apiClient from './apiClient.js';

const notificationService = {
  getMyNotifications: (unreadOnly = false) =>
    apiClient.get('/notifications', { params: { unreadOnly } }).then((r) => r.data),
  getUnreadCount: () => apiClient.get('/notifications/unread-count').then((r) => r.data),
  markAsRead: (id) => apiClient.patch(`/notifications/${id}/read`).then((r) => r.data),
  markAllAsRead: () => apiClient.patch('/notifications/read-all').then((r) => r.data),
  delete: (id) => apiClient.delete(`/notifications/${id}`).then((r) => r.data),
};

export default notificationService;

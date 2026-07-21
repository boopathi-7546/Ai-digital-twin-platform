package com.digitaltwin.platform.service;

import com.digitaltwin.platform.dto.notification.NotificationResponse;

import java.util.List;
import java.util.Map;

/**
 * Manages in-app notifications for a user: creation (used internally
 * by other services, e.g. "your resume analysis is ready"), listing,
 * and marking read.
 */
public interface NotificationService {

    NotificationResponse create(Long userId, String title, String message, String type);

    List<NotificationResponse> getMyNotifications(Long userId, boolean unreadOnly);

    Map<String, Long> getUnreadCount(Long userId);

    NotificationResponse markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId);

    void delete(Long userId, Long notificationId);
}

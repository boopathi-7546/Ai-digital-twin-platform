package com.digitaltwin.platform.controller;

import com.digitaltwin.platform.dto.notification.NotificationResponse;
import com.digitaltwin.platform.security.CustomUserDetails;
import com.digitaltwin.platform.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Notification endpoints available to any authenticated user (student
 * or admin) — notifications are scoped by the user's own id, not role.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
@Tag(name = "Notifications", description = "In-app notifications for the authenticated user")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List notifications for the authenticated user")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return ResponseEntity.ok(notificationService.getMyNotifications(principal.getId(), unreadOnly));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get the count of unread notifications")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(notificationService.getUnreadCount(principal.getId()));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.markAsRead(principal.getId(), notificationId));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Map<String, String>> markAllAsRead(@AuthenticationPrincipal CustomUserDetails principal) {
        notificationService.markAllAsRead(principal.getId());
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read."));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long notificationId) {
        notificationService.delete(principal.getId(), notificationId);
        return ResponseEntity.ok(Map.of("message", "Notification deleted successfully."));
    }
}

package com.milestonemetrics.backend.controller;

import com.milestonemetrics.backend.dto.BillReminderRequest;
import com.milestonemetrics.backend.dto.NotificationResponse;
import com.milestonemetrics.backend.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** All of the authenticated user's notifications, newest first. */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(Principal principal) {
        return ResponseEntity.ok(notificationService.listForUser(principal.getName()));
    }

    /** Count of the user's unread notifications (for the bell badge). */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(Principal principal) {
        return ResponseEntity.ok(Map.of("count", notificationService.unreadCount(principal.getName())));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id, Principal principal) {
        notificationService.markRead(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead(Principal principal) {
        notificationService.markAllRead(principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dismiss(@PathVariable Long id, Principal principal) {
        notificationService.dismiss(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /** Create a user-managed bill / payment reminder. */
    @PostMapping("/bill-reminder")
    public ResponseEntity<NotificationResponse> createBillReminder(
            @Valid @RequestBody BillReminderRequest request, Principal principal) {
        return ResponseEntity.ok(notificationService.createBillReminder(principal.getName(), request));
    }
}

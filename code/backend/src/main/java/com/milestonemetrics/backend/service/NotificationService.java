package com.milestonemetrics.backend.service;

import com.milestonemetrics.backend.dto.BillReminderRequest;
import com.milestonemetrics.backend.dto.NotificationResponse;
import com.milestonemetrics.backend.entity.Notification;
import com.milestonemetrics.backend.entity.NotificationType;
import com.milestonemetrics.backend.entity.User;
import com.milestonemetrics.backend.repository.NotificationRepository;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a notification for an event, but only once per (user, dedupeKey).
     * Returns the created notification, or null if one already existed.
     * A null dedupeKey means "always create" (e.g. user-created bill reminders).
     */
    public Notification createIfAbsent(User user, NotificationType type, String title,
                                       String message, String dedupeKey, LocalDate dueDate) {
        if (dedupeKey != null && notificationRepository.existsByUserAndDedupeKey(user, dedupeKey)) {
            return null;
        }
        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setDedupeKey(dedupeKey);
        n.setDueDate(dueDate);
        try {
            return notificationRepository.save(n);
        } catch (DataIntegrityViolationException e) {
            // Lost a race on the unique (user, dedupe_key) constraint — safe to ignore.
            return null;
        }
    }

    public Notification createIfAbsent(User user, NotificationType type, String title,
                                       String message, String dedupeKey) {
        return createIfAbsent(user, type, title, message, dedupeKey, null);
    }

    // ---------- user-facing API ----------

    public List<NotificationResponse> listForUser(String email) {
        User user = requireUser(email);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(NotificationResponse::new)
                .toList();
    }

    public long unreadCount(String email) {
        return notificationRepository.countByUserAndReadFalse(requireUser(email));
    }

    public void markRead(String email, Long id) {
        User user = requireUser(email);
        Notification n = notificationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!n.isRead()) {
            n.setRead(true);
            notificationRepository.save(n);
        }
    }

    public void markAllRead(String email) {
        User user = requireUser(email);
        List<Notification> unread = notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .filter(n -> !n.isRead())
                .toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public void dismiss(String email, Long id) {
        User user = requireUser(email);
        Notification n = notificationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notificationRepository.delete(n);
    }

    public NotificationResponse createBillReminder(String email, BillReminderRequest request) {
        User user = requireUser(email);
        String amountPart = request.getAmount() != null
                ? String.format(" — ₹%,.0f", request.getAmount()) : "";
        String note = request.getNote() != null && !request.getNote().isBlank()
                ? request.getNote() : "Payment due on " + request.getDueDate();
        Notification n = createIfAbsent(user, NotificationType.BILL_REMINDER,
                request.getTitle() + amountPart, note, null, request.getDueDate());
        return new NotificationResponse(n);
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}

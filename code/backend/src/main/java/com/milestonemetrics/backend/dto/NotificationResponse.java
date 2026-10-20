package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.Notification;
import com.milestonemetrics.backend.entity.NotificationType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private boolean read;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.type = n.getType();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.read = n.isRead();
        this.dueDate = n.getDueDate();
        this.createdAt = n.getCreatedAt();
    }

    public Long getId() { return id; }
    public NotificationType getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

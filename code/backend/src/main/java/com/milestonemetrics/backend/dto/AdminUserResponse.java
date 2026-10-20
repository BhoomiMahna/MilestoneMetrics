package com.milestonemetrics.backend.dto;

import com.milestonemetrics.backend.entity.User;

import java.time.LocalDateTime;

/** Safe projection of a user for the admin panel — never exposes the password. */
public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    public AdminUserResponse(User u) {
        this.id = u.getId();
        this.name = u.getName();
        this.email = u.getEmail();
        this.role = u.getRole().name();
        this.createdAt = u.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

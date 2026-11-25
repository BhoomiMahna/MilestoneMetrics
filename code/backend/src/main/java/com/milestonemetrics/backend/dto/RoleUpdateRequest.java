package com.milestonemetrics.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleUpdateRequest {

    @NotBlank(message = "Role is required (USER or ADMIN)")
    private String role;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

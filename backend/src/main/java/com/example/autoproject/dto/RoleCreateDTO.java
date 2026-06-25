package com.example.autoproject.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateDTO(
        @NotBlank(message = "Role name is required")
        String roleName,

        @NotBlank(message = "Role code is required")
        String roleCode,

        String description,

        Integer sortOrder
) {
}

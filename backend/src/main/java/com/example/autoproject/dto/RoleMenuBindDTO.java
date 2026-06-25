package com.example.autoproject.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RoleMenuBindDTO(
        @NotEmpty(message = "Menu IDs must not be empty")
        List<Long> menuIds
) {
}

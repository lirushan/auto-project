package com.example.autoproject.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UserRoleBindDTO(
        @NotEmpty(message = "Role IDs must not be empty")
        List<Long> roleIds
) {
}

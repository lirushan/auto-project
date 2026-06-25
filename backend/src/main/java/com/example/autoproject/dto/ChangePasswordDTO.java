package com.example.autoproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDTO(
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 6) String newPassword
) {}

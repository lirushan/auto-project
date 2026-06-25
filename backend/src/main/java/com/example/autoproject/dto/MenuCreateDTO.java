package com.example.autoproject.dto;

import jakarta.validation.constraints.NotBlank;

public record MenuCreateDTO(
        Long parentId,

        @NotBlank(message = "Menu name is required")
        String menuName,

        @NotBlank(message = "Menu type is required")
        String menuType,

        String path,

        String component,

        String icon,

        String permission,

        Integer sortOrder
) {
}

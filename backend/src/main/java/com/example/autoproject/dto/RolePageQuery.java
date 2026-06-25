package com.example.autoproject.dto;

public record RolePageQuery(
        Integer page,
        Integer size,
        String keyword
) {
}

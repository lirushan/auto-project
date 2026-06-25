package com.example.autoproject.dto;

public record UserPageQuery(
        Integer page,
        Integer size,
        String keyword
) {
}

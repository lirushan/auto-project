package com.example.autoproject.dto;

public record CourseUpdateDTO(
        String courseName,
        String description,
        String timePeriod,
        Integer quota,
        java.math.BigDecimal passScore,
        String examDesc,
        Integer status,
        Integer sortOrder
) {}

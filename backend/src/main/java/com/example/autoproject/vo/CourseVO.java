package com.example.autoproject.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseVO(
        Long id,
        String courseName,
        String description,
        String timePeriod,
        Integer quota,
        BigDecimal passScore,
        String examDesc,
        Integer status,
        Integer sortOrder,
        Long enrolledCount,
        Integer remaining
) {}

package com.example.autoproject.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RegistrationVO(
        Long id,
        String studentName,
        String studentId,
        Long courseId,
        String courseName,
        LocalDateTime registerTime,
        Integer rankNum,
        Integer status,
        LocalDateTime examTime,
        BigDecimal examScore,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {}

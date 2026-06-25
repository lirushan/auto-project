package com.example.autoproject.vo;

import java.time.LocalDateTime;

public record RoleVO(
        Long id,
        String roleName,
        String roleCode,
        String description,
        Integer status,
        Integer sortOrder,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}

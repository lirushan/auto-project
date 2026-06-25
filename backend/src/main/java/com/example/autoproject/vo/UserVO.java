package com.example.autoproject.vo;

import java.time.LocalDateTime;

public record UserVO(
        Long id,
        String username,
        String email,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}

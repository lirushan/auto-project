package com.example.autoproject.dto;

import java.util.List;

public record ReviewBatchDTO(
        List<Long> ids,
        String action
) {}

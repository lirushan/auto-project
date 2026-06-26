package com.example.autoproject.service;

import com.example.autoproject.dto.ReviewResultDTO;

public interface RegistrationService {
    /** 报名 (per-course Redis ZSET 入队 + MySQL 持久化) */
    Integer register(Long courseId, String studentName, String studentId);

    /** 批量审核通过 */
    int approveBatch(Long courseId, java.util.List<Long> regIds);

    /** 批量审核拒绝 (含递补逻辑) */
    ReviewResultDTO rejectBatch(Long courseId, java.util.List<Long> regIds);

    /** 考核 (随机打分模拟 60% 通过率) */
    void exam(Long regId);

    /** 获取录取名单 */
    java.util.List<String> getAdmitted();

    /** 获取当前排队号 */
    Long getQueueCount();

    /** 清理测试数据 */
    void clearAll();
}

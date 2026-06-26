package com.example.autoproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.autoproject.common.Result;
import com.example.autoproject.entity.Registration;
import com.example.autoproject.mapper.RegistrationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BizController {

    private final RegistrationMapper registrationMapper;
    private final JdbcTemplate jdbcTemplate;

    // === 学生列表 ===
    @GetMapping("/students")
    public Result<IPage<Map<String, Object>>> listStudents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        String where = "WHERE deleted = 0";
        if (keyword != null && !keyword.isBlank()) {
            where += " AND (student_name LIKE '%" + keyword + "%' OR id_card LIKE '%" + keyword + "%')";
        }
        var p = new Page<Map<String, Object>>(page, size);
        var total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM biz_student " + where, Long.class);
        if (total == null) total = 0L;
        p.setTotal(total);
        var records = jdbcTemplate.queryForList(
            "SELECT id, student_name, id_card, phone, grade, school, status, create_time FROM biz_student " + where + " ORDER BY id DESC LIMIT " + ((page-1)*size) + "," + size);
        p.setRecords(records);
        return Result.ok(p);
    }

    // === 报名记录列表 ===
    @GetMapping("/registrations")
    public Result<IPage<Registration>> listRegistrations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long courseId) {
        var qw = new LambdaQueryWrapper<Registration>();
        if (status != null) qw.eq(Registration::getStatus, status);
        if (courseId != null) qw.eq(Registration::getCourseId, courseId);
        qw.orderByDesc(Registration::getRegisterTime);
        return Result.ok(registrationMapper.selectPage(new Page<>(page, size), qw));
    }

    // === 报名统计 ===
    @GetMapping("/registrations/stats")
    public Result<Map<String, Object>> registrationStats() {
        var stats = jdbcTemplate.queryForList(
            "SELECT status, COUNT(*) as cnt FROM biz_registration WHERE deleted = 0 GROUP BY status ORDER BY status");
        var queueSize = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM biz_registration WHERE deleted = 0 AND status IN (0,1)", Long.class);
        var admitted = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM biz_registration WHERE deleted = 0 AND status = 2", Long.class);
        var supply = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM biz_registration WHERE deleted = 0 AND status = 4", Long.class);
        return Result.ok(Map.of(
            "details", stats,
            "queueCount", queueSize != null ? queueSize : 0,
            "admittedCount", admitted != null ? admitted : 0,
            "supplyCount", supply != null ? supply : 0
        ));
    }

    // === 考核配置 ===
    @GetMapping("/exam-config")
    public Result<Map<String, Object>> getExamConfig() {
        var config = jdbcTemplate.queryForMap(
            "SELECT * FROM biz_exam_config WHERE deleted = 0 AND status = 1 ORDER BY id DESC LIMIT 1");
        return Result.ok(config != null ? config : Map.of());
    }

    @PutMapping("/exam-config")
    public Result<Void> updateExamConfig(@RequestBody Map<String, Object> config) {
        jdbcTemplate.update(
            "UPDATE biz_exam_config SET quota=?, pass_score=? WHERE id=1",
            config.get("quota"), config.get("passScore"));
        return Result.ok();
    }
}

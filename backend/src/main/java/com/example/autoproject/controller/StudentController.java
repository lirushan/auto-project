package com.example.autoproject.controller;

import com.example.autoproject.common.BusinessException;
import com.example.autoproject.common.JwtUtil;
import com.example.autoproject.common.Result;
import com.example.autoproject.dto.EnrollRequest;
import com.example.autoproject.dto.SmsRequest;
import com.example.autoproject.dto.StudentLoginRequest;
import com.example.autoproject.service.RegistrationService;
import com.example.autoproject.service.impl.SmsService;
import com.example.autoproject.vo.EnrollResultVO;
import com.example.autoproject.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final SmsService smsService;
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final RegistrationService registrationService;

    /** 发送验证码 (无需登录) */
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody SmsRequest req) {
        smsService.sendCode(req.phone());
        return new Result<>(200, "验证码已发送", null);
    }

    /** 验证码登录/注册 (无需登录) */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody StudentLoginRequest req) {
        if (!smsService.verifyCode(req.phone(), req.code())) {
            return Result.fail(400, "验证码错误或已过期");
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, student_name, phone FROM biz_student WHERE phone = ? AND deleted = 0", req.phone());

        Long studentId;
        String studentName;

        if (!rows.isEmpty()) {
            Map<String, Object> row = rows.get(0);
            studentId = ((Number) row.get("id")).longValue();
            studentName = (String) row.get("student_name");
        } else {
            String phone = req.phone();
            studentName = phone.length() >= 7 ? phone.substring(0, 7) : phone;
            jdbcTemplate.update("INSERT INTO biz_student (student_name, phone) VALUES (?, ?)",
                    studentName, phone);
            studentId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        }

        String token = jwtUtil.generateStudentToken(studentId, studentName);
        return Result.ok(Map.of(
                "token", token,
                "studentId", studentId,
                "studentName", studentName
        ));
    }

    /** 获取个人资料 (需登录) */
    @GetMapping("/profile")
    public Result<StudentVO> profile(@RequestAttribute("studentId") Long studentId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, student_name, phone, grade, school, status FROM biz_student WHERE id = ? AND deleted = 0",
                studentId);
        if (rows.isEmpty()) {
            throw new BusinessException(404, "学生不存在");
        }
        Map<String, Object> row = rows.get(0);
        StudentVO vo = new StudentVO();
        vo.setId(((Number) row.get("id")).longValue());
        vo.setStudentName((String) row.get("student_name"));
        vo.setPhone((String) row.get("phone"));
        vo.setGrade((String) row.get("grade"));
        vo.setSchool((String) row.get("school"));
        vo.setStatus(row.get("status") != null ? ((Number) row.get("status")).intValue() : null);
        return Result.ok(vo);
    }

    /** 报名 (需登录) - 支持一人报多课 */
    @PostMapping("/enroll")
    public Result<EnrollResultVO> enroll(
            @RequestAttribute("studentId") Long studentId,
            @RequestAttribute("studentName") String studentName,
            @RequestBody EnrollRequest req) {
        // 检查同学生同课程是否已报名
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM biz_registration WHERE student_key_id = ? AND course_id = ? AND deleted = 0",
                Long.class, studentId, req.courseId());
        if (count != null && count > 0) {
            throw new BusinessException(400, "您已报名该课程，请勿重复报名");
        }

        Integer rankNum = registrationService.register(req.courseId(), studentName, "S" + studentId);

        // 关联 student_key_id
        jdbcTemplate.update(
                "UPDATE biz_registration SET student_key_id = ? WHERE student_id = ? AND course_id = ?",
                studentId, "S" + studentId, req.courseId());

        // 查询报名状态
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT status FROM biz_registration WHERE student_key_id = ? AND course_id = ? AND deleted = 0",
                studentId, req.courseId());
        Integer status = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("status")).intValue();

        String msg = switch (status) {
            case 1 -> "报名成功，已进入待审核名单";
            case 2 -> "报名成功，已通过审核";
            case 4 -> "报名成功，已递补进入";
            default -> "报名成功，当前为排队状态";
        };
        return Result.ok(new EnrollResultVO(rankNum, status, msg));
    }

    /** 我的报名记录 (需登录) */
    @GetMapping("/my-registrations")
    public Result<List<Map<String, Object>>> myRegistrations(@RequestAttribute("studentId") Long studentId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT r.id, r.student_name, r.student_id, r.course_id, r.register_time, " +
                "r.rank_num, r.status, r.exam_time, r.exam_score, r.create_time, r.update_time, " +
                "c.course_name FROM biz_registration r " +
                "LEFT JOIN biz_course c ON r.course_id = c.id AND c.deleted = 0 " +
                "WHERE r.student_key_id = ? AND r.deleted = 0 ORDER BY r.id DESC",
                studentId);
        if (rows.isEmpty()) {
            return Result.ok(List.of());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            result.add(new java.util.LinkedHashMap<>(row));
        }
        return Result.ok(result);
    }

    /** 可选课程列表 (需登录) */
    @GetMapping("/courses")
    public Result<List<Map<String, Object>>> courses() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, course_name, description, time_period, quota, pass_score, exam_desc, status, sort_order " +
                "FROM biz_course WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC, id DESC");
        if (rows.isEmpty()) {
            return Result.ok(List.of());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new java.util.LinkedHashMap<>(row);
            // enrolled count
            Long courseId = ((Number) row.get("id")).longValue();
            Long enrolled = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM biz_registration WHERE course_id = ? AND deleted = 0", Long.class, courseId);
            item.put("enrolledCount", enrolled != null ? enrolled : 0);
            Integer quota = row.get("quota") != null ? ((Number) row.get("quota")).intValue() : 0;
            item.put("remaining", quota - (enrolled != null ? enrolled.intValue() : 0));
            result.add(item);
        }
        return Result.ok(result);
    }
}

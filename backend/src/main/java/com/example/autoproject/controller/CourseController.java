package com.example.autoproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.common.Result;
import com.example.autoproject.dto.CourseCreateDTO;
import com.example.autoproject.dto.CourseUpdateDTO;
import com.example.autoproject.dto.ReviewBatchDTO;
import com.example.autoproject.dto.ReviewResultDTO;
import com.example.autoproject.entity.Registration;
import com.example.autoproject.mapper.RegistrationMapper;
import com.example.autoproject.service.CourseService;
import com.example.autoproject.service.RegistrationService;
import com.example.autoproject.vo.CourseVO;
import com.example.autoproject.vo.RegistrationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final RegistrationService registrationService;
    private final RegistrationMapper registrationMapper;
    private final JdbcTemplate jdbcTemplate;

    @PostMapping
    public Result<CourseVO> createCourse(@RequestBody CourseCreateDTO dto) {
        return Result.ok(courseService.createCourse(dto));
    }

    @GetMapping
    public Result<IPage<CourseVO>> listCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.ok(courseService.pageCourses(page, size, status));
    }

    @GetMapping("/{id}")
    public Result<CourseVO> getCourse(@PathVariable Long id) {
        return Result.ok(courseService.getCourse(id));
    }

    @PutMapping("/{id}")
    public Result<CourseVO> updateCourse(@PathVariable Long id, @RequestBody CourseUpdateDTO dto) {
        return Result.ok(courseService.updateCourse(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.ok();
    }

    @PostMapping("/{id}/review")
    public Result<ReviewResultDTO> review(@PathVariable Long id, @RequestBody ReviewBatchDTO dto) {
        if ("approve".equals(dto.action())) {
            int reviewed = registrationService.approveBatch(id, dto.ids());
            return Result.ok(new ReviewResultDTO(reviewed, 0));
        } else if ("reject".equals(dto.action())) {
            return Result.ok(registrationService.rejectBatch(id, dto.ids()));
        } else {
            throw new BusinessException(400, "无效的审核操作: " + dto.action());
        }
    }

    @GetMapping("/{id}/registrations")
    public Result<IPage<RegistrationVO>> listRegistrations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Registration::getCourseId, id);
        if (status != null) {
            wrapper.eq(Registration::getStatus, status);
        }
        wrapper.orderByAsc(Registration::getRankNum);
        IPage<Registration> regPage = registrationMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(regPage.convert(this::toRegistrationVO));
    }

    private RegistrationVO toRegistrationVO(Registration reg) {
        String courseName = null;
        if (reg.getCourseId() != null) {
            var row = jdbcTemplate.queryForMap(
                    "SELECT course_name FROM biz_course WHERE id = ? AND deleted = 0", reg.getCourseId());
            if (row != null) {
                courseName = (String) row.get("course_name");
            }
        }
        return new RegistrationVO(
                reg.getId(),
                reg.getStudentName(),
                reg.getStudentId(),
                reg.getCourseId(),
                courseName,
                reg.getRegisterTime(),
                reg.getRankNum(),
                reg.getStatus(),
                reg.getExamTime(),
                reg.getExamScore(),
                reg.getCreateTime(),
                reg.getUpdateTime()
        );
    }
}

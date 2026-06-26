package com.example.autoproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.dto.CourseCreateDTO;
import com.example.autoproject.dto.CourseUpdateDTO;
import com.example.autoproject.entity.Course;
import com.example.autoproject.mapper.CourseMapper;
import com.example.autoproject.service.CourseService;
import com.example.autoproject.vo.CourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public IPage<CourseVO> pageCourses(int page, int size, Integer status) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Course::getStatus, status);
        }
        wrapper.orderByAsc(Course::getSortOrder).orderByDesc(Course::getId);

        IPage<Course> coursePage = courseMapper.selectPage(new Page<>(page, size), wrapper);
        return coursePage.convert(this::toVO);
    }

    @Override
    public CourseVO createCourse(CourseCreateDTO dto) {
        Course course = new Course();
        course.setCourseName(dto.courseName());
        course.setDescription(dto.description());
        course.setTimePeriod(dto.timePeriod());
        course.setQuota(dto.quota() != null ? dto.quota() : 100);
        course.setPassScore(dto.passScore());
        course.setExamDesc(dto.examDesc());
        course.setStatus(dto.status() != null ? dto.status() : 1);
        course.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.insert(course);
        return toVO(course);
    }

    @Override
    public CourseVO updateCourse(Long id, CourseUpdateDTO dto) {
        Course course = getByIdOrThrow(id);
        if (dto.courseName() != null) course.setCourseName(dto.courseName());
        if (dto.description() != null) course.setDescription(dto.description());
        if (dto.timePeriod() != null) course.setTimePeriod(dto.timePeriod());
        if (dto.quota() != null) course.setQuota(dto.quota());
        if (dto.passScore() != null) course.setPassScore(dto.passScore());
        if (dto.examDesc() != null) course.setExamDesc(dto.examDesc());
        if (dto.status() != null) course.setStatus(dto.status());
        if (dto.sortOrder() != null) course.setSortOrder(dto.sortOrder());
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);
        return toVO(course);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = getByIdOrThrow(id);
        courseMapper.deleteById(course.getId());
    }

    @Override
    public CourseVO getCourse(Long id) {
        Course course = getByIdOrThrow(id);
        return toVO(course);
    }

    private Course getByIdOrThrow(Long id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        return course;
    }

    private CourseVO toVO(Course course) {
        Long enrolledCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM biz_registration WHERE course_id = ? AND deleted = 0",
                Long.class, course.getId());
        if (enrolledCount == null) enrolledCount = 0L;
        int remaining = course.getQuota() != null ? course.getQuota() - enrolledCount.intValue() : 0;
        return new CourseVO(
                course.getId(),
                course.getCourseName(),
                course.getDescription(),
                course.getTimePeriod(),
                course.getQuota(),
                course.getPassScore(),
                course.getExamDesc(),
                course.getStatus(),
                course.getSortOrder(),
                enrolledCount,
                remaining
        );
    }
}

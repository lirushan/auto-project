package com.example.autoproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.autoproject.dto.CourseCreateDTO;
import com.example.autoproject.dto.CourseUpdateDTO;
import com.example.autoproject.vo.CourseVO;

public interface CourseService {
    IPage<CourseVO> pageCourses(int page, int size, Integer status);

    CourseVO createCourse(CourseCreateDTO dto);

    CourseVO updateCourse(Long id, CourseUpdateDTO dto);

    void deleteCourse(Long id);

    CourseVO getCourse(Long id);
}

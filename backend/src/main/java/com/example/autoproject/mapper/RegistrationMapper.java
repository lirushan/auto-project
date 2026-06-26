package com.example.autoproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.autoproject.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
    @Select("SELECT * FROM biz_registration WHERE student_key_id = #{studentKeyId} AND course_id = #{courseId} AND deleted = 0")
    List<Registration> findByStudentAndCourse(Long studentKeyId, Long courseId);
}

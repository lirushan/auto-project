package com.example.autoproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.autoproject.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

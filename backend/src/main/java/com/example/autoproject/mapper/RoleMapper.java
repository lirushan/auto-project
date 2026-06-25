package com.example.autoproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.autoproject.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("<script>SELECT role_code FROM sys_role WHERE id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<String> selectRoleCodesByIds(@Param("ids") List<Long> ids);
}

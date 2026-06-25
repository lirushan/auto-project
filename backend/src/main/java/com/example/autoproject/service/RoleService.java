package com.example.autoproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.autoproject.dto.RoleCreateDTO;
import com.example.autoproject.dto.RoleMenuBindDTO;
import com.example.autoproject.dto.RolePageQuery;
import com.example.autoproject.dto.RoleUpdateDTO;
import com.example.autoproject.entity.Role;
import com.example.autoproject.vo.RoleVO;

import java.util.List;

public interface RoleService extends IService<Role> {

    IPage<RoleVO> pageRoles(RolePageQuery query);

    RoleVO createRole(RoleCreateDTO dto);

    RoleVO updateRole(Long id, RoleUpdateDTO dto);

    void deleteRole(Long id);

    void bindMenus(Long roleId, RoleMenuBindDTO dto);

    List<Long> getRoleMenus(Long roleId);
}

package com.example.autoproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.dto.RoleCreateDTO;
import com.example.autoproject.dto.RoleMenuBindDTO;
import com.example.autoproject.dto.RolePageQuery;
import com.example.autoproject.dto.RoleUpdateDTO;
import com.example.autoproject.entity.Role;
import com.example.autoproject.entity.RoleMenu;
import com.example.autoproject.entity.UserRole;
import com.example.autoproject.mapper.RoleMapper;
import com.example.autoproject.mapper.RoleMenuMapper;
import com.example.autoproject.mapper.UserRoleMapper;
import com.example.autoproject.service.RoleService;
import com.example.autoproject.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public IPage<RoleVO> pageRoles(RolePageQuery query) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (query.keyword() != null && !query.keyword().isBlank()) {
            wrapper.like(Role::getRoleName, query.keyword());
        }
        wrapper.orderByAsc(Role::getSortOrder);

        int page = query.page() != null ? query.page() : 1;
        int size = query.size() != null ? query.size() : 10;

        IPage<Role> rolePage = page(new Page<>(page, size), wrapper);
        return rolePage.convert(this::toVO);
    }

    @Override
    public RoleVO createRole(RoleCreateDTO dto) {
        Role role = new Role();
        role.setRoleName(dto.roleName());
        role.setRoleCode(dto.roleCode());
        role.setDescription(dto.description());
        role.setStatus(1);
        role.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        save(role);
        return toVO(role);
    }

    @Override
    public RoleVO updateRole(Long id, RoleUpdateDTO dto) {
        Role role = getRoleByIdOrThrow(id);
        role.setRoleName(dto.roleName());
        role.setRoleCode(dto.roleCode());
        role.setDescription(dto.description());
        role.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : role.getSortOrder());
        role.setUpdateTime(LocalDateTime.now());
        updateById(role);
        return toVO(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleByIdOrThrow(id);

        Long count = userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id));
        if (count > 0) {
            throw new BusinessException(400, "角色已关联用户，无法删除");
        }

        removeById(role.getId());
    }

    @Override
    @Transactional
    public void bindMenus(Long roleId, RoleMenuBindDTO dto) {
        getRoleByIdOrThrow(roleId);

        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));

        List<RoleMenu> roleMenus = dto.menuIds().stream().map(menuId -> {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            rm.setCreateTime(LocalDateTime.now());
            return rm;
        }).toList();

        for (RoleMenu rm : roleMenus) {
            roleMenuMapper.insert(rm);
        }
    }

    @Override
    public List<Long> getRoleMenus(Long roleId) {
        getRoleByIdOrThrow(roleId);

        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        return roleMenus.stream().map(RoleMenu::getMenuId).toList();
    }

    private Role getRoleByIdOrThrow(Long id) {
        Role role = getById(id);
        if (role == null) {
            throw new BusinessException(404, "Role not found");
        }
        return role;
    }

    private RoleVO toVO(Role role) {
        return new RoleVO(
                role.getId(),
                role.getRoleName(),
                role.getRoleCode(),
                role.getDescription(),
                role.getStatus(),
                role.getSortOrder(),
                role.getCreateTime(),
                role.getUpdateTime()
        );
    }
}

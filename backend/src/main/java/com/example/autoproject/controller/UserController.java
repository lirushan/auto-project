package com.example.autoproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.common.Result;
import com.example.autoproject.dto.ChangePasswordDTO;
import com.example.autoproject.dto.UserCreateDTO;
import com.example.autoproject.dto.UserPageQuery;
import com.example.autoproject.dto.UserRoleBindDTO;
import com.example.autoproject.dto.UserUpdateDTO;
import com.example.autoproject.entity.Role;
import com.example.autoproject.entity.UserRole;
import com.example.autoproject.mapper.RoleMapper;
import com.example.autoproject.mapper.UserRoleMapper;
import com.example.autoproject.service.UserService;
import com.example.autoproject.vo.RoleVO;
import com.example.autoproject.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @GetMapping
    public Result<IPage<UserVO>> listUsers(UserPageQuery query) {
        return Result.ok(userService.pageUsers(query));
    }

    @PostMapping
    public Result<UserVO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        return Result.ok(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    public Result<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return Result.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @Valid @RequestBody UserRoleBindDTO dto) {
        userService.assignRoles(id, dto);
        return Result.ok();
    }

    @GetMapping("/{id}/roles")
    public Result<List<RoleVO>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = userRoleMapper.selectList(
                        new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id))
                .stream()
                .map(UserRole::getRoleId)
                .toList();

        if (roleIds.isEmpty()) {
            return Result.ok(List.of());
        }

        List<Role> roles = roleMapper.selectList(
                new LambdaQueryWrapper<Role>().in(Role::getId, roleIds));

        List<RoleVO> vos = roles.stream()
                .map(role -> new RoleVO(
                        role.getId(),
                        role.getRoleName(),
                        role.getRoleCode(),
                        role.getDescription(),
                        role.getStatus(),
                        role.getSortOrder(),
                        role.getCreateTime(),
                        role.getUpdateTime()
                ))
                .toList();

        return Result.ok(vos);
    }

    @PutMapping("/{id}/password")
    public Result<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(id, dto);
        return Result.ok();
    }
}

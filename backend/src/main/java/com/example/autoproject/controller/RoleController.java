package com.example.autoproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.autoproject.common.Result;
import com.example.autoproject.dto.RoleCreateDTO;
import com.example.autoproject.dto.RoleMenuBindDTO;
import com.example.autoproject.dto.RolePageQuery;
import com.example.autoproject.dto.RoleUpdateDTO;
import com.example.autoproject.service.RoleService;
import com.example.autoproject.vo.RoleVO;
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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public Result<IPage<RoleVO>> listRoles(RolePageQuery query) {
        return Result.ok(roleService.pageRoles(query));
    }

    @PostMapping
    public Result<RoleVO> createRole(@Valid @RequestBody RoleCreateDTO dto) {
        return Result.ok(roleService.createRole(dto));
    }

    @PutMapping("/{id}")
    public Result<RoleVO> updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateDTO dto) {
        return Result.ok(roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.ok();
    }

    @PutMapping("/{id}/menus")
    public Result<Void> bindMenus(@PathVariable Long id, @Valid @RequestBody RoleMenuBindDTO dto) {
        roleService.bindMenus(id, dto);
        return Result.ok();
    }

    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        return Result.ok(roleService.getRoleMenus(id));
    }
}

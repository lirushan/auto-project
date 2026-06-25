package com.example.autoproject.controller;

import com.example.autoproject.common.Result;
import com.example.autoproject.dto.MenuCreateDTO;
import com.example.autoproject.dto.MenuUpdateDTO;
import com.example.autoproject.service.MenuService;
import com.example.autoproject.vo.MenuVO;
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
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public Result<List<MenuVO>> listMenus() {
        return Result.ok(menuService.listMenuTree());
    }

    @PostMapping
    public Result<MenuVO> createMenu(@Valid @RequestBody MenuCreateDTO dto) {
        return Result.ok(menuService.createMenu(dto));
    }

    @PutMapping("/{id}")
    public Result<MenuVO> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuUpdateDTO dto) {
        return Result.ok(menuService.updateMenu(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.ok();
    }
}

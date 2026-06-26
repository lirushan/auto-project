package com.example.autoproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.dto.MenuCreateDTO;
import com.example.autoproject.dto.MenuUpdateDTO;
import com.example.autoproject.entity.Menu;
import com.example.autoproject.mapper.MenuMapper;
import com.example.autoproject.service.MenuService;
import com.example.autoproject.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<MenuVO> listMenuTree() {
        List<Menu> allMenus = list();
        List<MenuVO> menuVOs = allMenus.stream().map(this::toVO).collect(Collectors.toList());
        return buildTree(menuVOs);
    }

    @Override
    public MenuVO createMenu(MenuCreateDTO dto) {
        Menu menu = new Menu();
        menu.setParentId(dto.parentId() != null ? dto.parentId() : 0L);
        menu.setMenuName(dto.menuName());
        menu.setMenuType(dto.menuType() != null ? Integer.parseInt(dto.menuType()) : 1);
        menu.setPath(dto.path());
        menu.setComponent(dto.component());
        menu.setIcon(dto.icon());
        menu.setPermission(dto.permission());
        menu.setSortOrder(dto.sortOrder() != null ? dto.sortOrder() : 0);
        menu.setStatus(1);
        menu.setVisible(1);
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        save(menu);
        return toVO(menu);
    }

    @Override
    public MenuVO updateMenu(Long id, MenuUpdateDTO dto) {
        Menu menu = getMenuByIdOrThrow(id);
        if (dto.parentId() != null) menu.setParentId(dto.parentId());
        menu.setMenuName(dto.menuName());
        menu.setMenuType(dto.menuType() != null ? Integer.parseInt(dto.menuType()) : 1);
        menu.setPath(dto.path());
        menu.setComponent(dto.component());
        menu.setIcon(dto.icon());
        menu.setPermission(dto.permission());
        if (dto.sortOrder() != null) menu.setSortOrder(dto.sortOrder());
        menu.setUpdateTime(LocalDateTime.now());
        updateById(menu);
        return toVO(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        long childCount = lambdaQuery().eq(Menu::getParentId, id).count();
        if (childCount > 0) throw new BusinessException(400, "存在子菜单无法删除");
        removeById(id);
    }

    private MenuVO toVO(Menu m) {
        MenuVO vo = new MenuVO();
        vo.setId(m.getId());
        vo.setParentId(m.getParentId());
        vo.setMenuName(m.getMenuName());
        vo.setMenuType(m.getMenuType());
        vo.setPath(m.getPath());
        vo.setComponent(m.getComponent());
        vo.setIcon(m.getIcon());
        vo.setPermission(m.getPermission());
        vo.setSortOrder(m.getSortOrder());
        vo.setStatus(m.getStatus());
        vo.setVisible(m.getVisible());
        vo.setCreateTime(m.getCreateTime());
        vo.setUpdateTime(m.getUpdateTime());
        vo.setChildren(new ArrayList<>());
        return vo;
    }

    private List<MenuVO> buildTree(List<MenuVO> items) {
        Map<Long, MenuVO> map = new HashMap<>();
        List<MenuVO> roots = new ArrayList<>();
        for (MenuVO item : items) map.put(item.getId(), item);
        for (MenuVO item : items) {
            Long pid = item.getParentId();
            if (pid == null || pid == 0) roots.add(item);
            else {
                MenuVO parent = map.get(pid);
                if (parent != null) {
                    if (parent.getChildren() == null) parent.setChildren(new ArrayList<>());
                    parent.getChildren().add(item);
                }
            }
        }
        return roots;
    }

    private Menu getMenuByIdOrThrow(Long id) {
        Menu menu = getById(id);
        if (menu == null) throw new BusinessException(404, "菜单不存在");
        return menu;
    }
}

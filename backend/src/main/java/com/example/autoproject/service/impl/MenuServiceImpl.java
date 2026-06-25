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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<MenuVO> listMenuTree() {
        List<Menu> allMenus = list();
        List<MenuVO> menuVOs = allMenus.stream().map(this::toVO).toList();
        return buildTree(menuVOs);
    }

    @Override
    public MenuVO createMenu(MenuCreateDTO dto) {
        Menu menu = new Menu();
        menu.setParentId(dto.parentId() != null ? dto.parentId() : 0L);
        menu.setMenuName(dto.menuName());
        menu.setMenuType(parseMenuType(dto.menuType()));
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
        if (dto.parentId() != null) {
            menu.setParentId(dto.parentId());
        }
        menu.setMenuName(dto.menuName());
        menu.setMenuType(parseMenuType(dto.menuType()));
        menu.setPath(dto.path());
        menu.setComponent(dto.component());
        menu.setIcon(dto.icon());
        menu.setPermission(dto.permission());
        if (dto.sortOrder() != null) {
            menu.setSortOrder(dto.sortOrder());
        }
        menu.setUpdateTime(LocalDateTime.now());
        updateById(menu);
        return toVO(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        Menu menu = getMenuByIdOrThrow(id);

        Long childCount = lambdaQuery().eq(Menu::getParentId, id).count();
        if (childCount > 0) {
            throw new BusinessException(400, "菜单下存在子菜单，无法删除");
        }

        removeById(menu.getId());
    }

    private MenuVO toVO(Menu menu) {
        return new MenuVO(
                menu.getId(),
                menu.getParentId(),
                menu.getMenuName(),
                menu.getMenuType(),
                menu.getPath(),
                menu.getComponent(),
                menu.getIcon(),
                menu.getPermission(),
                menu.getSortOrder(),
                menu.getStatus(),
                menu.getVisible(),
                menu.getCreateTime(),
                menu.getUpdateTime(),
                new ArrayList<>()
        );
    }

    private List<MenuVO> buildTree(List<MenuVO> menuVOs) {
        Map<Long, List<MenuVO>> parentMap = menuVOs.stream()
                .collect(Collectors.groupingBy(MenuVO::parentId));

        menuVOs.forEach(menu -> {
            List<MenuVO> children = parentMap.getOrDefault(menu.id(), new ArrayList<>());
            // Replace the empty list with actual children
            List<MenuVO> mutableChildren = new ArrayList<>(children);
            if (!mutableChildren.isEmpty()) {
                // Can't modify record fields, so the children field in MenuVO remains as-is
            }
        });

        // Filter top-level menus and build tree recursively
        return menuVOs.stream()
                .filter(menu -> menu.parentId() == null || menu.parentId() == 0L)
                .map(menu -> buildChildren(menu, parentMap))
                .toList();
    }

    private MenuVO buildChildren(MenuVO parent, Map<Long, List<MenuVO>> parentMap) {
        List<MenuVO> children = parentMap.getOrDefault(parent.id(), new ArrayList<>())
                .stream()
                .map(child -> buildChildren(child, parentMap))
                .toList();

        return new MenuVO(
                parent.id(),
                parent.parentId(),
                parent.menuName(),
                parent.menuType(),
                parent.path(),
                parent.component(),
                parent.icon(),
                parent.permission(),
                parent.sortOrder(),
                parent.status(),
                parent.visible(),
                parent.createTime(),
                parent.updateTime(),
                children
        );
    }

    private Menu getMenuByIdOrThrow(Long id) {
        Menu menu = getById(id);
        if (menu == null) {
            throw new BusinessException(404, "Menu not found");
        }
        return menu;
    }

    private int parseMenuType(String menuType) {
        return switch (menuType) {
            case "M" -> 1;
            case "C" -> 2;
            case "F" -> 3;
            default -> Integer.parseInt(menuType);
        };
    }
}

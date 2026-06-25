package com.example.autoproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.autoproject.dto.MenuCreateDTO;
import com.example.autoproject.dto.MenuUpdateDTO;
import com.example.autoproject.entity.Menu;
import com.example.autoproject.vo.MenuVO;

import java.util.List;

public interface MenuService extends IService<Menu> {

    List<MenuVO> listMenuTree();

    MenuVO createMenu(MenuCreateDTO dto);

    MenuVO updateMenu(Long id, MenuUpdateDTO dto);

    void deleteMenu(Long id);
}

package com.example.autoproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.autoproject.entity.Menu;
import com.example.autoproject.vo.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    @Select("SELECT DISTINCT rm.menu_id FROM sys_role_menu rm WHERE rm.role_id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>")
    List<Long> selectMenuIdsByRoleIds(@Param("ids") List<Long> roleIds);

    @Select("SELECT id, parent_id, menu_name as menuName, menu_type as menuType, path, component, icon, permission, sort_order as sortOrder, status, visible, create_time as createTime, update_time as updateTime FROM sys_menu WHERE deleted = 0 AND visible = 1 AND status = 1 ORDER BY sort_order")
    List<MenuVO> selectAllAsTree();

    @Select("<script>SELECT id, parent_id, menu_name as menuName, menu_type as menuType, path, component, icon, permission, sort_order as sortOrder, status, visible, create_time as createTime, update_time as updateTime FROM sys_menu WHERE deleted = 0 AND visible = 1 AND status = 1 AND id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> ORDER BY sort_order</script>")
    List<MenuVO> selectAsTreeByIds(@Param("ids") List<Long> menuIds);
}

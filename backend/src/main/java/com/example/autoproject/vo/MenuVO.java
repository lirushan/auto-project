package com.example.autoproject.vo;

import java.time.LocalDateTime;
import java.util.List;

public class MenuVO {
    private Long id;
    private Long parentId;
    private String menuName;
    private Integer menuType;
    private String path;
    private String component;
    private String icon;
    private String permission;
    private Integer sortOrder;
    private Integer status;
    private Integer visible;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<MenuVO> children;

    public MenuVO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public Integer getMenuType() { return menuType; }
    public void setMenuType(Integer menuType) { this.menuType = menuType; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getVisible() { return visible; }
    public void setVisible(Integer visible) { this.visible = visible; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public List<MenuVO> getChildren() { return children; }
    public void setChildren(List<MenuVO> children) { this.children = children; }
}

package com.example.autoproject.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}

-- ============================================================
-- Auto-Project 数据库初始化脚本
-- 由 Docker MySQL 容器首次启动时自动执行
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) DEFAULT '$2a$10$dummyhashedpassword',
    email VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1 COMMENT '0=禁用,1=启用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_username (username),
    INDEX idx_email (email)
);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) DEFAULT '' COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '0=禁用,1=启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_role_code (role_code)
);

CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID，0为顶级',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type TINYINT DEFAULT 1 COMMENT '0=目录,1=菜单,2=按钮',
    path VARCHAR(200) DEFAULT '' COMMENT '路由路径',
    component VARCHAR(200) DEFAULT '' COMMENT '前端组件路径',
    icon VARCHAR(50) DEFAULT '' COMMENT '图标',
    permission VARCHAR(100) DEFAULT '' COMMENT '权限标识',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '0=禁用,1=启用',
    visible TINYINT DEFAULT 1 COMMENT '0=隐藏,1=显示',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_menu_parent (parent_id)
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_menu (role_id, menu_id)
);

-- 插入默认角色
INSERT IGNORE INTO sys_role (id, role_name, role_code, description, sort_order) VALUES
(1, '超级管理员', 'admin', '系统最高权限', 1),
(2, '普通用户', 'user', '基础访问权限', 2);

-- 插入默认菜单
INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, permission, sort_order) VALUES
(1, 0, '系统管理', 0, '/system', '', 'Setting', '', 1),
(2, 1, '用户管理', 1, '/system/users', 'UserListPage', 'User', 'user:list', 1),
(3, 1, '角色管理', 1, '/system/roles', 'RoleListPage', 'UserFilled', 'role:list', 2),
(4, 1, '菜单管理', 1, '/system/menus', 'MenuListPage', 'Menu', 'menu:list', 3);

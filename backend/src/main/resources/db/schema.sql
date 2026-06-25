-- =====================================================
-- Auto Project - 数据库初始化脚本
-- RBAC 模型：sys_user / sys_role / sys_menu / sys_user_role / sys_role_menu
-- =====================================================

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) DEFAULT '$2a$10$dummyhashedpassword',
    email VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1 COMMENT '0=禁用,1=启用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) DEFAULT '' COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '0=禁用,1=启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    KEY idx_menu_parent (parent_id)
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

package com.example.autoproject.vo;

import java.util.List;

public record LoginVO(String token, Long userId, String username, List<String> roles, List<MenuVO> menus) {}

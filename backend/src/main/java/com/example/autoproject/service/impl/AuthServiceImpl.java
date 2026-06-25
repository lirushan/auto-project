package com.example.autoproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.common.JwtUtil;
import com.example.autoproject.entity.User;
import com.example.autoproject.mapper.MenuMapper;
import com.example.autoproject.mapper.RoleMapper;
import com.example.autoproject.mapper.UserMapper;
import com.example.autoproject.mapper.UserRoleMapper;
import com.example.autoproject.service.AuthService;
import com.example.autoproject.vo.LoginVO;
import com.example.autoproject.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // Get user roles
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
        List<String> roleCodes = roleMapper.selectRoleCodesByIds(roleIds);

        // Get user menus (all menus if SYSTEM role)
        List<MenuVO> menus;
        if (roleCodes.contains("SYSTEM")) {
            menus = menuMapper.selectAllAsTree();
        } else {
            List<Long> menuIds = menuMapper.selectMenuIdsByRoleIds(roleIds);
            menus = menuMapper.selectAsTreeByIds(menuIds);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginVO(token, user.getId(), user.getUsername(), roleCodes, menus);
    }
}

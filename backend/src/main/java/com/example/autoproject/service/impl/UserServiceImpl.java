package com.example.autoproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.dto.ChangePasswordDTO;
import com.example.autoproject.dto.UserCreateDTO;
import com.example.autoproject.dto.UserPageQuery;
import com.example.autoproject.dto.UserRoleBindDTO;
import com.example.autoproject.dto.UserUpdateDTO;
import com.example.autoproject.entity.User;
import com.example.autoproject.entity.UserRole;
import com.example.autoproject.mapper.UserMapper;
import com.example.autoproject.mapper.UserRoleMapper;
import com.example.autoproject.service.UserService;
import com.example.autoproject.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public User getUserByIdOrThrow(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        return user;
    }

    @Override
    public IPage<UserVO> pageUsers(UserPageQuery query) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (query.keyword() != null && !query.keyword().isBlank()) {
            wrapper.like(User::getUsername, query.keyword());
        }
        wrapper.orderByDesc(User::getId);

        int page = query.page() != null ? query.page() : 1;
        int size = query.size() != null ? query.size() : 10;

        IPage<User> userPage = page(new Page<>(page, size), wrapper);
        return userPage.convert(this::toVO);
    }

    @Override
    public UserVO createUser(UserCreateDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        save(user);
        return toVO(user);
    }

    @Override
    public UserVO updateUser(Long id, UserUpdateDTO dto) {
        User user = getUserByIdOrThrow(id);
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
        return toVO(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserByIdOrThrow(id);
        removeById(user.getId());
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, UserRoleBindDTO dto) {
        getUserByIdOrThrow(userId);

        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));

        dto.roleIds().forEach(roleId -> {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            ur.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(ur);
        });
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = getById(userId);
        if (user == null || !passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        updateById(user);
    }

    private UserVO toVO(User user) {
        return new UserVO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreateTime(),
                user.getUpdateTime()
        );
    }
}

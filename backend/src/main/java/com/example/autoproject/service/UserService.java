package com.example.autoproject.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.autoproject.dto.ChangePasswordDTO;
import com.example.autoproject.dto.UserCreateDTO;
import com.example.autoproject.dto.UserPageQuery;
import com.example.autoproject.dto.UserRoleBindDTO;
import com.example.autoproject.dto.UserUpdateDTO;
import com.example.autoproject.entity.User;
import com.example.autoproject.vo.UserVO;

public interface UserService extends IService<User> {

    IPage<UserVO> pageUsers(UserPageQuery query);

    UserVO createUser(UserCreateDTO dto);

    UserVO updateUser(Long id, UserUpdateDTO dto);

    void deleteUser(Long id);

    void assignRoles(Long userId, UserRoleBindDTO dto);

    void changePassword(Long userId, ChangePasswordDTO dto);
}

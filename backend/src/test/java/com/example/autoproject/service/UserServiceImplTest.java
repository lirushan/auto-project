package com.example.autoproject.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.dto.UserCreateDTO;
import com.example.autoproject.dto.UserPageQuery;
import com.example.autoproject.dto.UserUpdateDTO;
import com.example.autoproject.entity.User;
import com.example.autoproject.mapper.UserMapper;
import com.example.autoproject.mapper.UserRoleMapper;
import com.example.autoproject.service.impl.UserServiceImpl;
import com.example.autoproject.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        // @RequiredArgsConstructor + @InjectMocks 不设置父类 baseMapper，需手动注入
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setCreateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        user.setUpdateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    @Test
    void pageUsers_shouldReturnPage_withKeyword() {
        UserPageQuery query = new UserPageQuery(1, 10, "test");
        Page<User> userPage = new Page<>(1, 10);
        userPage.setRecords(java.util.List.of(user));
        userPage.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(userPage);

        IPage<UserVO> result = userService.pageUsers(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("testuser", result.getRecords().get(0).username());
    }

    @Test
    void pageUsers_shouldReturnAll_whenKeywordIsEmpty() {
        UserPageQuery query = new UserPageQuery(1, 10, null);
        Page<User> userPage = new Page<>(1, 10);
        userPage.setRecords(java.util.List.of(user));
        userPage.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(userPage);

        IPage<UserVO> result = userService.pageUsers(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    void pageUsers_shouldReturnAll_whenKeywordIsBlank() {
        UserPageQuery query = new UserPageQuery(1, 10, "   ");
        Page<User> userPage = new Page<>(1, 10);
        userPage.setRecords(java.util.List.of());
        userPage.setTotal(0);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(userPage);

        IPage<UserVO> result = userService.pageUsers(query);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
    }

    @Test
    void pageUsers_shouldUseDefaults_whenPageAndSizeAreNull() {
        UserPageQuery query = new UserPageQuery(null, null, null);
        Page<User> userPage = new Page<>(1, 10);
        userPage.setRecords(java.util.List.of(user));
        userPage.setTotal(1);

        when(userMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(userPage);

        IPage<UserVO> result = userService.pageUsers(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    void createUser_shouldSaveAndReturnVO() {
        UserCreateDTO dto = new UserCreateDTO("newuser", "new@example.com");

        when(userMapper.insert(any(User.class))).thenReturn(1);

        UserVO result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("newuser", result.username());
        assertEquals("new@example.com", result.email());
        assertNotNull(result.createTime());
        assertNotNull(result.updateTime());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertEquals("newuser", captor.getValue().getUsername());
        assertEquals("new@example.com", captor.getValue().getEmail());
    }

    @Test
    void updateUser_shouldUpdateAndReturnVO() {
        UserUpdateDTO dto = new UserUpdateDTO("updated", "updated@example.com");

        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UserVO result = userService.updateUser(1L, dto);

        assertNotNull(result);
        assertEquals("updated", result.username());
        assertEquals("updated@example.com", result.email());
    }

    @Test
    void updateUser_shouldThrow_whenUserNotFound() {
        UserUpdateDTO dto = new UserUpdateDTO("updated", "updated@example.com");

        when(userMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.updateUser(999L, dto));
        assertEquals(404, ex.getCode());
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void deleteUser_shouldSoftDelete() {
        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.deleteById(1L)).thenReturn(1);

        userService.deleteUser(1L);

        verify(userMapper).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrow_whenUserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.deleteUser(999L));
        assertEquals(404, ex.getCode());
        assertEquals("User not found", ex.getMessage());
        verify(userMapper, never()).deleteById(any(Long.class));
    }

    @Test
    void getUserByIdOrThrow_shouldReturnUser() {
        when(userMapper.selectById(1L)).thenReturn(user);

        User result = userService.getUserByIdOrThrow(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserByIdOrThrow_shouldThrow_whenNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.getUserByIdOrThrow(999L));
        assertEquals(404, ex.getCode());
        assertEquals("User not found", ex.getMessage());
    }
}

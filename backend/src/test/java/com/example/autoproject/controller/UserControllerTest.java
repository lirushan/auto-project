package com.example.autoproject.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.autoproject.common.BusinessException;
import com.example.autoproject.dto.UserCreateDTO;
import com.example.autoproject.dto.UserPageQuery;
import com.example.autoproject.dto.UserUpdateDTO;
import com.example.autoproject.service.UserService;
import com.example.autoproject.vo.UserVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void listUsers_shouldReturnPaginatedUsers() throws Exception {
        UserVO vo = new UserVO(1L, "testuser", "test@example.com",
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0));
        IPage<UserVO> page = new Page<>(1, 10);
        page.setRecords(java.util.List.of(vo));
        page.setTotal(1);

        when(userService.pageUsers(any(UserPageQuery.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "1")
                        .param("size", "10")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.records[0].username").value("testuser"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserCreateDTO dto = new UserCreateDTO("newuser", "new@example.com");
        UserVO vo = new UserVO(1L, "newuser", "new@example.com",
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(vo);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }

    @Test
    void createUser_shouldReturn400_whenValidationFails() throws Exception {
        UserCreateDTO dto = new UserCreateDTO("", "invalid-email");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO("updated", "updated@example.com");
        UserVO vo = new UserVO(1L, "updated", "updated@example.com",
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.updateUser(eq(1L), any(UserUpdateDTO.class))).thenReturn(vo);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("updated"));
    }

    @Test
    void updateUser_shouldReturn404_whenUserNotFound() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO("updated", "updated@example.com");

        when(userService.updateUser(eq(999L), any(UserUpdateDTO.class)))
                .thenThrow(new BusinessException(404, "User not found"));

        mockMvc.perform(put("/api/v1/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void deleteUser_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_shouldReturn404_whenUserNotFound() throws Exception {
        doThrow(new BusinessException(404, "User not found"))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/v1/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}

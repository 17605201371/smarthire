/*
package com.resume_ai_project.smarthire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.resume_ai_project.smarthire.common.utils.JwtUtils;
import com.resume_ai_project.smarthire.entity.Role;
import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser; // 添加缺失的导入
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserControllerV2.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils; // 模拟 JwtUtils，避免真实依赖

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 注册 Java 8 时间模块，处理 LocalDateTime 序列化
        objectMapper.registerModule(new JavaTimeModule());
        // 如果需要将时间序列化为字符串（而非时间戳），取消下面注释
        // objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")  // 模拟 EMPLOYER 角色
    public void testGetAllUsers() throws Exception {
        User user1 = new User(1L, "user1", "user1@test.com", "12345", Role.CANDIDATE);
        User user2 = new User(2L, "user2", "user2@test.com", "12345", Role.EMPLOYER);

        when(userService.findAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/v2/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")  // 添加认证模拟
    public void testGetUserById() throws Exception {
        User user = new User(1L, "testuser", "test@test.com", "12345", Role.CANDIDATE);

        when(userService.findUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v2/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")  // 添加认证模拟
    public void testCreateUser() throws Exception {
        // 准备输入数据（id 和 createTime 由后端生成，不传）
        User inputUser = new User();
        inputUser.setUsername("李四");
        inputUser.setPhone("lisi@test.com");
        inputUser.setPassword("123456");
        inputUser.setRole(Role.CANDIDATE);

        // 模拟保存后返回的用户对象（包含生成 ID 和 createTime）
        User mockSavedUser = new User();
        mockSavedUser.setId(1L);
        mockSavedUser.setUsername("李四");
        mockSavedUser.setPhone("lisi@test.com");
        mockSavedUser.setPassword("123456");
        mockSavedUser.setRole(Role.CANDIDATE);
        // createTime 会在实体中被 @CreationTimestamp 自动填充，mock 对象可以手动设置一个值（可选）
        // mockSavedUser.setCreateTime(LocalDateTime.now());

        when(userService.saveUser(any(User.class))).thenReturn(mockSavedUser);

        mockMvc.perform(post("/api/v2/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("李四"))
                .andExpect(jsonPath("$.phone").value("lisi@test.com"))
                .andExpect(jsonPath("$.createTime").exists()); // 验证 createTime 字段存在
    }
}*/

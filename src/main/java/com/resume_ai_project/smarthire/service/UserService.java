package com.resume_ai_project.smarthire.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    // 根据 ID 查询（注意方法名要与 Controller 中一致：findUserById 或 findById）
    public Optional<User> findUserById(Long id) {
        User user = userMapper.selectById(id);
        return Optional.ofNullable(user);
    }
    // 保存用户（插入）
    public User saveUser(User user) {
        userMapper.insert(user);
        return user;  // MyBatis-Plus 插入后会回填 id，直接返回
    }

    // 分页查询所有用户
    public Page<User> findAllUsers(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        // 无条件查询
        return userMapper.selectPage(page, null);
    }

    // 根据用户名判断是否存在
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    // 根据手机号判断是否存在
    public boolean existsByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectCount(wrapper) > 0;
    }
}
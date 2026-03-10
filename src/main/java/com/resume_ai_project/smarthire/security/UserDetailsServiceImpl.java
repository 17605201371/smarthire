package com.resume_ai_project.smarthire.security;

import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;  // 改为 UserMapper

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 支持用户名或手机号登录
        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
                        .or()
                        .eq(User::getPhone, username)
        );
            
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        return UserDetailsImpl.build(user);
    }
}
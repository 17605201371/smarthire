package com.resume_ai_project.smarthire.controller;
import com.resume_ai_project.smarthire.common.utils.JwtUtils;
import com.resume_ai_project.smarthire.dto.*;
import com.resume_ai_project.smarthire.entity.Role;
import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.security.UserDetailsImpl;
import com.resume_ai_project.smarthire.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 {
    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);

    private final UserService userService;

    /**
     * 构造器注入 - 推荐方式
     */
    @Autowired
    public UserControllerV2(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("=== 开始登录 ===");
            logger.info("用户名/手机号：{}", loginRequest.getUsername());
            
            // 1. 创建认证令牌
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            // 2. 执行认证
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. 将认证信息存入安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. 生成 JWT
            String jwt = jwtUtils.generateJwtToken(authentication);

            // 5. 获取用户详情
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            logger.info("登录成功，用户 ID: {}, 用户名：{}, 角色：{}", 
                userDetails.getId(), userDetails.getUsername(), userDetails.getAuthorities());

            // 6. 返回响应
            return ResponseEntity.ok(new LoginResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getPhone(),
                    userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "")
            ));
        } catch (Exception e) {
            logger.error("❌ 登录失败 - 完整异常堆栈:", e);
            logger.error("错误类型：{}", e.getClass().getName());
            logger.error("错误消息：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("登录失败：" + e.getMessage()));
        }
    }

    // 其他方法（如 getAllUsers, getUserById, createUser）保持不变
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest signUpRequest) {
        if (signUpRequest.getRole() == null) {
            signUpRequest.setRole(Role.CANDIDATE); // 默认求职者
        }
        // 1. 检查用户名是否已存在
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("用户名已存在！"));
        }

        // 2. 检查邮箱是否已存在
        if (userService.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity.badRequest().body(new MessageResponse("手机号已被注册！"));
        }

        // 3. 创建新用户
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPhone(signUpRequest.getPhone());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword())); // 加密密码
        user.setRole(signUpRequest.getRole());
        // createTime 由 @CreationTimestamp 自动填充，无需手动设置

        // 4. 保存用户
        userService.saveUser(user);

        return ResponseEntity.ok(new MessageResponse("用户注册成功！"));
    }
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<User> page = userService.findAllUsers(pageNum, pageSize);
        return ResponseEntity.ok(page);
    }
}

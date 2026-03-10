package com.resume_ai_project.smarthire.common.utils;

import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 获取当前登录的用户实体（需在 Service 层事务中调用，或确保 User 已加载）
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            // 注意：这里返回的是 UserDetailsImpl 中持有的 User 对象，但可能只有 id 等基本信息。
            // 更好的做法是从数据库重新查询完整对象，或使用 @AuthenticationPrincipal 在 Controller 参数中注入。
            // 这里简化：我们假设 UserDetailsImpl 中有完整的 User 对象（需要在构造时传入）。
            return userDetails.getUser();
        }
        throw new RuntimeException("用户未登录");
    }
}
package com.resume_ai_project.smarthire.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.resume_ai_project.smarthire.entity.Application;
import com.resume_ai_project.smarthire.mapper.ApplicationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 职位申请服务
 */
@Service
public class ApplicationService extends ServiceImpl<ApplicationMapper, Application> {
    
    /**
     * 申请职位
     */
    public Application applyPosition(Long positionId, Long resumeId, Long userId, 
                                     String coverLetter, String expectedSalary, String availableDate) {
        Application application = Application.builder()
                .positionId(positionId)
                .resumeId(resumeId)
                .userId(userId)
                .coverLetter(coverLetter)
                .expectedSalary(expectedSalary)
                .availableDate(availableDate)
                .status("APPLIED")
                .build();
        
        save(application);
        return application;
    }
    
    /**
     * 获取职位的所有申请
     */
    public List<Application> getApplicationsByPositionId(Long positionId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getPositionId, positionId)
                .orderByDesc(Application::getCreateTime);
        return list(wrapper);
    }
    
    /**
     * 获取用户的申请
     */
    public List<Application> getApplicationsByUserId(Long userId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getUserId, userId)
                .orderByDesc(Application::getCreateTime);
        return list(wrapper);
    }
    
    /**
     * 根据 ID 获取申请
     */
    public Application getApplicationById(Long id) {
        return getById(id);
    }
    
    /**
     * 更新申请
     */
    public void updateApplication(Application application) {
        updateById(application);
    }
}

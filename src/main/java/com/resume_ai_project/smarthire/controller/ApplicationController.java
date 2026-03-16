package com.resume_ai_project.smarthire.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.resume_ai_project.smarthire.dto.MessageResponse;
import com.resume_ai_project.smarthire.entity.Application;
import com.resume_ai_project.smarthire.entity.Resume;
import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.security.UserDetailsImpl;
import com.resume_ai_project.smarthire.service.ApplicationService;
import com.resume_ai_project.smarthire.service.ResumeService;
import com.resume_ai_project.smarthire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 职位申请控制器
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ResumeService resumeService;
    
    /**
     * 申请职位
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> createApplication(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Map<String, Object> applicationData) {
        try {
            Long positionId = Long.valueOf(applicationData.get("positionId").toString());
            Long resumeId = Long.valueOf(applicationData.get("resumeId").toString());
            String coverLetter = (String) applicationData.get("coverLetter");
            String expectedSalary = (String) applicationData.get("expectedSalary");
            String availableDate = (String) applicationData.get("availableDate");
            
            // 验证简历所有权
            Resume resume = resumeService.getById(resumeId);
            if (resume == null || !resume.getUserId().equals(userDetails.getId())) {
                return ResponseEntity.badRequest().body(new MessageResponse("简历不存在或无权使用"));
            }
            
            // 创建申请
            Application application = applicationService.applyPosition(
                    positionId, resumeId, userDetails.getId(), 
                    coverLetter, expectedSalary, availableDate);
            
            return ResponseEntity.ok(Map.of(
                    "id", application.getId(),
                    "message", "申请成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("申请失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取职位收到的申请（企业用户）
     */
    @GetMapping("/position/{positionId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<Application>> getApplicationsByPosition(
            @PathVariable Long positionId) {
        List<Application> applications = applicationService.getApplicationsByPositionId(positionId);
        return ResponseEntity.ok(applications);
    }
    
    /**
     * 获取职位的简历列表（用于企业查看收到的简历）
     */
    @GetMapping("/resumes/for-position")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getResumesForPosition(
            @RequestParam Long positionId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 获取该职位的所有申请
            List<com.resume_ai_project.smarthire.entity.Application> applications = applicationService.getApplicationsByPositionId(positionId);
            
            // 提取简历 ID 列表
            List<Long> resumeIds = applications.stream()
                    .map(com.resume_ai_project.smarthire.entity.Application::getResumeId)
                    .toList();
            
            // 构建申请 ID 到简历 ID 的映射
            Map<Long, com.resume_ai_project.smarthire.entity.Application> applicationMap = applications.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            com.resume_ai_project.smarthire.entity.Application::getResumeId,
                            app -> app
                    ));
            
            if (resumeIds.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "records", List.of(),
                        "total", 0
                ));
            }
            
            // 根据简历 ID 查询简历信息
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Resume> resultPage = 
                    resumeService.findResumesByIds(resumeIds, pageNum, pageSize);
            
            // 为每个简历添加申请 ID 和状态
            var enrichedRecords = resultPage.getRecords().stream()
                    .map(resume -> {
                        var app = applicationMap.get(resume.getId());
                        Map<String, Object> resumeMap = new java.util.HashMap<>();
                        resumeMap.put("id", resume.getId());
                        resumeMap.put("candidateName", resume.getCandidateName());
                        resumeMap.put("candidatePhone", resume.getCandidatePhone());
                        resumeMap.put("candidateEmail", resume.getCandidateEmail());
                        resumeMap.put("expectedJob", resume.getExpectedJob());
                        resumeMap.put("skills", resume.getSkills());
                        resumeMap.put("fileUrl", resume.getFileUrl());
                        resumeMap.put("createTime", resume.getCreateTime());
                        resumeMap.put("applicationId", app != null ? app.getId() : null);
                        resumeMap.put("status", app != null ? app.getStatus() : "APPLIED");
                        return resumeMap;
                    })
                    .toList();
            
            return ResponseEntity.ok(Map.of(
                    "records", enrichedRecords,
                    "total", resultPage.getTotal()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("查询简历失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取用户的申请记录（求职者）
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<Application>> getMyApplications(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Application> applications = applicationService.getApplicationsByUserId(userDetails.getId());
        return ResponseEntity.ok(applications);
    }
    
    /**
     * 邀请面试（企业用户）
     */
    @PostMapping("/{id}/interview")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> inviteInterview(
            @PathVariable Long id,
            @RequestBody Map<String, String> interviewData) {
        try {
            // 获取申请
            Application application = applicationService.getApplicationById(id);
            if (application == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("申请不存在"));
            }
            
            // 更新状态为面试
            application.setStatus("INTERVIEW");
            applicationService.updateApplication(application);
            
            // TODO: 发送邮件或短信通知候选人
            
            return ResponseEntity.ok(Map.of(
                    "message", "已发送面试邀请",
                    "status", "INTERVIEW"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("邀请失败：" + e.getMessage()));
        }
    }
}

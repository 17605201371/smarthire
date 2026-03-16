package com.resume_ai_project.smarthire.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.resume_ai_project.smarthire.dto.MessageResponse;
import com.resume_ai_project.smarthire.entity.Resume;
import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.security.UserDetailsImpl;
import com.resume_ai_project.smarthire.service.ResumeService;
import com.resume_ai_project.smarthire.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserService userService;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * 上传简历并解析（不保存，仅返回解析结果）
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            User currentUser = userService.findUserById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            // 调用 uploadAndParse 方法，只解析不保存
            com.resume_ai_project.smarthire.dto.ParseResult parseResult = resumeService.uploadAndParse(file, currentUser);
            // 返回解析结果给前端
            Map<String, Object> result = new HashMap<>();
            result.put("name", parseResult.getName());
            result.put("phone", parseResult.getPhone());
            result.put("email", parseResult.getEmail());
            result.put("skills", String.join(",", parseResult.getSkills()));
            result.put("educationJson", objectMapper.writeValueAsString(parseResult.getEducationList()));
            result.put("workJson", objectMapper.writeValueAsString(parseResult.getWorkExperienceList()));
            result.put("expectedJob", parseResult.getExpectedJob());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("简历上传失败", e);
            return ResponseEntity.badRequest().body(new MessageResponse("上传失败：" + e.getMessage()));
        }
    }

    /**
     * 获取当前用户的简历列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Page<Resume>> getMyResumes(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        // 从数据库获取完整的 User 实体
        User currentUser = userService.findUserById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 调用 Service 层分页查询
        Page<Resume> page = resumeService.findByUser(currentUser, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }

    /**
     * 保存简历（用户确认信息后）
     */
    @PostMapping("/save")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> saveResume(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Map<String, Object> resumeData) {
        try {
            User currentUser = userService.findUserById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 从请求体中提取数据
            String fileName = (String) resumeData.get("fileName");
            String fileUrl = (String) resumeData.get("fileUrl");
            String rawText = (String) resumeData.get("rawText");
            
            // 构建 ParseResult
            com.resume_ai_project.smarthire.dto.ParseResult parseResult = new com.resume_ai_project.smarthire.dto.ParseResult();
            parseResult.setName((String) resumeData.get("name"));
            parseResult.setPhone((String) resumeData.get("phone"));
            parseResult.setEmail((String) resumeData.get("email"));
            parseResult.setExpectedJob((String) resumeData.get("expectedJob"));
            
            // 处理 skills
            String skillsStr = (String) resumeData.get("skills");
            if (skillsStr != null && !skillsStr.isEmpty()) {
                parseResult.setSkills(List.of(skillsStr.split(",")));
            }
            
            // 保存简历
            Resume resume = resumeService.saveResume(currentUser, parseResult, fileUrl, rawText, fileName);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", resume.getId());
            result.put("name", resume.getCandidateName());
            result.put("phone", resume.getCandidatePhone());
            result.put("skills", resume.getSkills());
            result.put("expectedJob", resume.getExpectedJob());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("保存简历失败", e);
            return ResponseEntity.badRequest().body(new MessageResponse("保存失败：" + e.getMessage()));
        }
    }

    /**
     * 更新简历信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<?> updateResume(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Map<String, Object> updateData) {
        try {
            // 验证简历所有权
            Resume existingResume = resumeService.getById(id);
            if (existingResume == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("简历不存在"));
            }
            
            if (!existingResume.getUserId().equals(userDetails.getId())) {
                return ResponseEntity.status(403).body(new MessageResponse("无权操作该简历"));
            }

            // 更新字段
            if (updateData.containsKey("name")) {
                existingResume.setCandidateName((String) updateData.get("name"));
            }
            if (updateData.containsKey("phone")) {
                existingResume.setCandidatePhone((String) updateData.get("phone"));
            }
            if (updateData.containsKey("skills")) {
                existingResume.setSkills((String) updateData.get("skills"));
            }
            if (updateData.containsKey("expectedJob")) {
                existingResume.setExpectedJob((String) updateData.get("expectedJob"));
            }

            // 保存更新
            resumeService.updateResume(existingResume);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", existingResume.getId());
            result.put("name", existingResume.getCandidateName());
            result.put("phone", existingResume.getCandidatePhone());
            result.put("skills", existingResume.getSkills());
            result.put("expectedJob", existingResume.getExpectedJob());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("更新简历失败", e);
            return ResponseEntity.badRequest().body(new MessageResponse("更新失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取简历详情（根据 ID）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'EMPLOYER')")
    public ResponseEntity<?> getResumeDetail(@PathVariable Long id) {
        try {
            Resume resume = resumeService.getById(id);
            if (resume == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("简历不存在"));
            }
            
            // TODO: 这里可以添加权限验证，确保只有有权限的人才能查看
            
            return ResponseEntity.ok(resume);
        } catch (Exception e) {
            logger.error("获取简历详情失败", e);
            return ResponseEntity.badRequest().body(new MessageResponse("获取失败：" + e.getMessage()));
        }
    }
}
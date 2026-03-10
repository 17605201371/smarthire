package com.resume_ai_project.smarthire.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.resume_ai_project.smarthire.dto.MessageResponse;
import com.resume_ai_project.smarthire.entity.Position;
import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.security.UserDetailsImpl;
import com.resume_ai_project.smarthire.service.PositionService;
import com.resume_ai_project.smarthire.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 职位控制器
 */
@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    private PositionService positionService;

    @Autowired
    private UserService userService;

    /**
     * 发布新职位
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> createPosition(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Position position) {
        try {
            logger.info("=== 开始发布新职位 ===");
            logger.info("企业 ID: {}", userDetails.getId());
            logger.info("职位名称：{}", position.getPositionName());
            logger.info("薪资范围：{}", position.getSalaryRange());
            logger.info("工作地点：{}", position.getLocation());
            
            // 设置企业 ID
            position.setEmployerId(userDetails.getId());
            // 默认状态为发布中
            position.setStatus("PUBLISHED");
            
            Position savedPosition = positionService.savePosition(position);
            
            logger.info("职位发布成功，ID: {}", savedPosition.getId());
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", savedPosition.getId());
            result.put("positionName", savedPosition.getPositionName());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("❌ 发布职位失败 - 完整异常堆栈:", e);
            logger.error("错误消息：{}", e.getMessage());
            logger.error("错误类型：{}", e.getClass().getName());
            return ResponseEntity.badRequest().body(new MessageResponse("发布失败：" + e.getMessage()));
        }
    }

    /**
     * 更新职位
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> updatePosition(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Position position) {
        try {
            Position existingPosition = positionService.getPositionById(id);
            if (existingPosition == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("职位不存在"));
            }
            
            // 验证职位所有权
            if (!existingPosition.getEmployerId().equals(userDetails.getId())) {
                return ResponseEntity.status(403).body(new MessageResponse("无权操作该职位"));
            }
            
            // 更新字段
            existingPosition.setPositionName(position.getPositionName());
            existingPosition.setDescription(position.getDescription());
            existingPosition.setRequirements(position.getRequirements());
            existingPosition.setSalaryRange(position.getSalaryRange());
            existingPosition.setLocation(position.getLocation());
            existingPosition.setExperienceRequired(position.getExperienceRequired());
            existingPosition.setDegreeRequired(position.getDegreeRequired());
            existingPosition.setStatus(position.getStatus());
            
            positionService.updatePosition(existingPosition);
            
            return ResponseEntity.ok(new MessageResponse("更新成功"));
        } catch (Exception e) {
            logger.error("更新职位失败", e);
            return ResponseEntity.badRequest().body(new MessageResponse("更新失败：" + e.getMessage()));
        }
    }

    /**
     * 删除职位
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> deletePosition(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Position existingPosition = positionService.getPositionById(id);
            if (existingPosition == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("职位不存在"));
            }
            
            // 验证职位所有权
            if (!existingPosition.getEmployerId().equals(userDetails.getId())) {
                return ResponseEntity.status(403).body(new MessageResponse("无权操作该职位"));
            }
            
            positionService.deletePosition(id);
            
            return ResponseEntity.ok(new MessageResponse("删除成功"));
        } catch (Exception e) {
            logger.error("删除职位失败", e);
            return ResponseEntity.badRequest().body(new MessageResponse("删除失败：" + e.getMessage()));
        }
    }

    /**
     * 获取职位详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Position> getPositionById(@PathVariable Long id) {
        Position position = positionService.getPositionById(id);
        if (position == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(position);
    }

    /**
     * 获取当前企业发布的职位列表（分页）
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Page<Position>> getMyPositions(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        try {
            logger.info("获取企业用户 {} 的职位列表", userDetails.getId());
            Page<Position> page = positionService.findByEmployer(userDetails.getId(), pageNum, pageSize);
            logger.info("获取成功，共 {} 条记录", page.getTotal());
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            logger.error("获取职位列表失败", e);
            throw new RuntimeException("获取职位列表失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取所有发布中的职位（分页）- 求职者查看
     */
    @GetMapping("/published")
    public ResponseEntity<Page<Position>> getPublishedPositions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        try {
            logger.info("获取发布中的职位列表，pageNum: {}, pageSize: {}", pageNum, pageSize);
            Page<Position> page = positionService.findPublishedPositions(pageNum, pageSize);
            logger.info("获取成功，共 {} 条记录", page.getTotal());
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            logger.error("获取职位列表失败", e);
            throw new RuntimeException("获取职位列表失败：" + e.getMessage(), e);
        }
    }

    /**
     * 搜索职位
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Position>> searchPositions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String degree,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        try {
            logger.info("搜索职位 - keyword: {}, location: {}, degree: {}, pageNum: {}, pageSize: {}", 
                keyword, location, degree, pageNum, pageSize);
            
            Page<Position> page = positionService.searchPositions(keyword, location, degree, pageNum, pageSize);
            
            logger.info("搜索成功，共 {} 条记录", page.getTotal());
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            logger.error("搜索职位失败", e);
            throw new RuntimeException("搜索失败：" + e.getMessage(), e);
        }
    }
}

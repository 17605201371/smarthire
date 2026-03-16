package com.resume_ai_project.smarthire.controller;

import com.resume_ai_project.smarthire.entity.MatchResult;
import com.resume_ai_project.smarthire.entity.Position;
import com.resume_ai_project.smarthire.entity.Resume;
import com.resume_ai_project.smarthire.service.MatchService;
import com.resume_ai_project.smarthire.service.PositionService;
import com.resume_ai_project.smarthire.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 智能匹配控制器
 */
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private PositionService positionService;

    /**
     * 为职位推荐候选人（支持分页）
     */
    @GetMapping("/position/{positionId}/candidates")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<MatchResult>> getCandidatesForPosition(
            @PathVariable Long positionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            logger.info("为职位 {} 推荐候选人，页码：{}, 每页：{}", positionId, page, size);
            
            // 获取职位信息
            Position position = positionService.getPositionById(positionId);
           if (position == null) {
               return ResponseEntity.notFound().build();
            }
            
            // 获取所有简历
            List<Resume> resumes = resumeService.findAll();
            
            // 计算匹配
            List<MatchResult> results = matchService.matchCandidates(position, resumes);
            
            // 分页
            int total = results.size();
            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, total);
            
           if (fromIndex >= total) {
               return ResponseEntity.ok(Collections.emptyList());
            }
            
            List<MatchResult> pagedResults = results.subList(fromIndex, toIndex);
            
            logger.info("匹配完成，总共 {} 个候选人，返回第 {} 页 {} 条", total, page, pagedResults.size());
            
           return ResponseEntity.ok(pagedResults);
        } catch (Exception e) {
            logger.error("推荐候选人失败", e);
           return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 为求职者推荐职位
     */
    @GetMapping("/resume/{resumeId}/positions")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<MatchResult>> getPositionsForResume(
            @PathVariable Long resumeId) {
        try {
            logger.info("为简历 {} 推荐职位", resumeId);
            
            // 获取简历信息
            Resume resume = resumeService.getResumeById(resumeId);
            if (resume == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 获取所有发布中的职位
            List<Position> positions = positionService.findAllPublished();
            
            // 计算匹配
            List<MatchResult> results = matchService.matchPositions(resume, positions);
            
            logger.info("匹配完成，共 {} 个职位", results.size());
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("推荐职位失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 计算单个简历和职位的匹配度
     */
    @GetMapping("/calculate")
    public ResponseEntity<MatchResult> calculateMatch(
            @RequestParam Long resumeId,
            @RequestParam Long positionId) {
        try {
            logger.info("计算简历 {} 和职位 {} 的匹配度", resumeId, positionId);
            
            Resume resume = resumeService.getResumeById(resumeId);
            Position position = positionService.getPositionById(positionId);
            
            if (resume == null || position == null) {
                return ResponseEntity.notFound().build();
            }
            
            MatchResult result = matchService.calculateMatch(resume, position);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("计算匹配度失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

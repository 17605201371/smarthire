package com.resume_ai_project.smarthire.service;

import com.resume_ai_project.smarthire.entity.MatchDetail;
import com.resume_ai_project.smarthire.entity.MatchResult;
import com.resume_ai_project.smarthire.entity.Resume;
import com.resume_ai_project.smarthire.entity.Position;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 智能匹配服务
 */
@Service
public class MatchService {

    /**
     * 为职位匹配候选人
     * @param position 职位
     * @param resumes 简历列表
     * @return 匹配结果列表（按匹配度排序）
     */
    public List<MatchResult> matchCandidates(Position position, List<Resume> resumes) {
        List<MatchResult> results = new ArrayList<>();
        
        for (Resume resume: resumes) {
            MatchResult result = calculateMatch(resume, position);
            results.add(result);
        }
        
        // 按匹配分数降序排序
        results.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));
        
        return results;
    }

    /**
     * 为求职者匹配职位
     * @param resume 简历
     * @param positions 职位列表
     * @return 匹配结果列表（按匹配度排序）
     */
    public List<MatchResult> matchPositions(Resume resume, List<Position> positions) {
        List<MatchResult> results = new ArrayList<>();
        
        for (Position position : positions) {
            MatchResult result = calculateMatch(resume, position);
            results.add(result);
        }
        
        // 按匹配分数降序排序
        results.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));
        
        return results;
    }

    /**
     * 计算单个匹配结果（公开方法，供 Controller 调用）
     */
    public MatchResult calculateMatch(Resume resume, Position position) {
        MatchResult result = new MatchResult();
        result.setResumeId(resume.getId());
        result.setPositionId(position.getId());
        result.setCandidateName(resume.getCandidateName());
        result.setCandidatePhone(resume.getCandidatePhone());
        result.setPositionName(position.getPositionName());
        result.setSalaryRange(position.getSalaryRange());
        
        // 计算各维度匹配分数
        MatchDetail detail = MatchDetail.builder()
                .skillsScore(calculateSkillsScore(resume, position))
                .experienceScore(calculateExperienceScore(resume, position))
                .educationScore(calculateEducationScore(resume, position))
                .locationScore(calculateLocationScore(resume, position))
                .salaryScore(calculateSalaryScore(resume, position))
                .build();
        
        // 计算综合分数（加权平均）
        Double totalScore = calculateTotalScore(detail);
        
        result.setMatchScore(totalScore);
        result.setMatchDetail(detail);
        
        // 生成匹配优势和不匹配点
        generateMatchAnalysis(detail, result);
        
        return result;
    }

    /**
     * 计算技能匹配分数
     */
    private Double calculateSkillsScore(Resume resume, Position position) {
        String resumeSkills = resume.getSkills() != null ? resume.getSkills().toLowerCase() : "";
        String positionRequirements = position.getRequirements() != null ? position.getRequirements().toLowerCase() : "";
        
        // 提取技能关键词
        List<String> requiredSkills = extractSkills(positionRequirements);
       if (requiredSkills.isEmpty()) {
            return 80.0; // 如果职位没有明确要求，给基础分
        }
        
        int matchedSkills = 0;
        for (String skill : requiredSkills) {
           if (resumeSkills.contains(skill.toLowerCase())) {
               matchedSkills++;
            }
        }
        
        return Math.min(100.0, (double) matchedSkills/ requiredSkills.size() * 100);
    }

    /**
     * 计算经验匹配分数
     */
    private Double calculateExperienceScore(Resume resume, Position position) {
        String positionExperience = position.getExperienceRequired();
        
       if (positionExperience == null || positionExperience.isEmpty()) {
            return 80.0;
        }
        
        // 简单匹配经验要求
       if (positionExperience.contains("应届生")) {
            return 100.0;
        } else if (positionExperience.contains("1-3 年")) {
            return 90.0;
        } else if (positionExperience.contains("3-5 年")) {
            return 80.0;
        } else if (positionExperience.contains("5-10 年")) {
            return 70.0;
        } else if (positionExperience.contains("10 年以上")) {
            return 60.0;
        }
        
        return 80.0;
    }

    /**
     * 计算学历匹配分数
     */
    private Double calculateEducationScore(Resume resume, Position position) {
        String positionDegree = position.getDegreeRequired();
        
       if (positionDegree == null || positionDegree.isEmpty()) {
            return 80.0;
        }
        
        // 学历匹配逻辑
        switch (positionDegree) {
            case "大专":
                return 100.0;
            case "本科":
                return 90.0;
            case "硕士":
                return 80.0;
            case "博士":
                return 70.0;
            default:
                return 80.0;
        }
    }

    /**
     * 计算地点匹配分数
     */
    private Double calculateLocationScore(Resume resume, Position position) {
        // 暂时给基础分，后续可以根据简历中的期望地点进行匹配
        return 80.0;
    }

    /**
     * 计算薪资匹配分数
     */
    private Double calculateSalaryScore(Resume resume, Position position) {
        // 暂时给基础分，后续可以根据简历中的期望薪资进行匹配
        return 80.0;
    }

    /**
     * 计算总分（加权平均）
     */
    private Double calculateTotalScore(MatchDetail detail) {
        // 权重：技能 40%，经验 25%，学历 20%，地点 10%，薪资 5%
        double skillsWeight = 0.4;
        double experienceWeight = 0.25;
        double educationWeight = 0.2;
        double locationWeight = 0.1;
        double salaryWeight = 0.05;
        
        double totalScore = detail.getSkillsScore() * skillsWeight
                + detail.getExperienceScore() * experienceWeight
                + detail.getEducationScore() * educationWeight
                + detail.getLocationScore() * locationWeight
                + detail.getSalaryScore() * salaryWeight;
        
        return Math.round(totalScore * 100.0) / 100.0;
    }

    /**
     * 提取技能关键词
     */
    private List<String> extractSkills(String text) {
        List<String> skills = new ArrayList<>();
        
        // 常见技术关键词
        String[] commonSkills = {
            "java", "python", "javascript", "typescript", "go", "c++",
            "spring", "spring boot", "spring cloud", "django", "flask",
            "vue", "react", "angular", "html", "css",
            "mysql", "postgresql", "mongodb", "redis", "kafka",
            "docker", "kubernetes", "jenkins", "git",
            "linux", "nginx", "microservice"
        };
        
        for (String skill : commonSkills) {
           if (text.contains(skill)) {
                skills.add(skill);
            }
        }
        
        return skills;
    }

    /**
     * 生成匹配分析
     */
    private void generateMatchAnalysis(MatchDetail detail, MatchResult result) {
        StringBuilder strengths = new StringBuilder();
        StringBuilder weaknesses = new StringBuilder();
        
       if (detail.getSkillsScore() >= 80) {
            strengths.append("技能匹配度高; ");
        } else {
            weaknesses.append("技能匹配度有待提升; ");
        }
        
       if (detail.getExperienceScore() >= 80) {
            strengths.append("经验符合要求; ");
        } else {
            weaknesses.append("经验与岗位要求有差距; ");
        }
        
       if (detail.getEducationScore() >= 80) {
            strengths.append("学历达标; ");
        } else {
            weaknesses.append("学历未达到岗位要求; ");
        }
        
        result.getMatchDetail().setStrengths(strengths.toString());
        result.getMatchDetail().setWeaknesses(weaknesses.toString());
    }
}

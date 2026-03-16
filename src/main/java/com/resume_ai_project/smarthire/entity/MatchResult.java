 package com.resume_ai_project.smarthire.entity;

import lombok.Data;

/**
 * 匹配结果
 */
@Data
public class MatchResult {

    /**
     * 简历 ID
     */
    private Long resumeId;

    /**
     * 职位 ID
     */
    private Long positionId;

    /**
     * 匹配分数（0-100）
     */
    private Double matchScore;

    /**
     * 匹配详情
     */
    private MatchDetail matchDetail;

    /**
     * 候选人姓名
     */
    private String candidateName;

    /**
     * 候选人电话
     */
    private String candidatePhone;

    /**
     * 职位名称
     */
    private String positionName;

    /**
     * 薪资范围
     */
    private String salaryRange;
}

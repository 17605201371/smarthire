package com.resume_ai_project.smarthire.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 匹配详情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDetail {

    /**
     * 技能匹配分数（0-100）
     */
    private Double skillsScore;

    /**
     * 经验匹配分数（0-100）
     */
    private Double experienceScore;

    /**
     * 学历匹配分数（0-100）
     */
    private Double educationScore;

    /**
     * 地点匹配分数（0-100）
     */
    private Double locationScore;

    /**
     * 薪资匹配分数（0-100）
     */
    private Double salaryScore;

    /**
     * 匹配优势
     */
    private String strengths;

    /**
     * 不匹配点
     */
    private String weaknesses;
}

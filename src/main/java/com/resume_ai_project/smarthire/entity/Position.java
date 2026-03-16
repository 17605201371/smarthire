package com.resume_ai_project.smarthire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 职位实体
 */
@Data
@TableName("positions")
public class Position {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 职位名称
     */
    @TableField("position_name")
    private String positionName;

    /**
     * 职位描述
     */
    @TableField("description")
    private String description;

    /**
     * 职位要求（JSON 格式，包含技能列表等）
     */
    @TableField("requirements")
    private String requirements;

    /**
     * 薪资范围（如：15k-25k）
     */
    @TableField("salary_range")
    private String salaryRange;

    /**
     * 工作地点
     */
    @TableField("location")
    private String location;

    /**
     * 经验要求（如：3-5 年）
     */
    @TableField("experience_required")
    private String experienceRequired;

    /**
     * 学历要求（如：本科）
     */
    @TableField("degree_required")
    private String degreeRequired;

    /**
     * 发布企业 ID
     */
    @TableField("employer_id")
    private Long employerId;

    /**
     * 状态：PUBLISHED-发布中，CLOSED-已关闭
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 收到简历数量（非数据库字段，用于统计）
     */
    @TableField(exist = false)
    private Long resumeCount = 0L;
}

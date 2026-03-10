package com.resume_ai_project.smarthire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resumes")
public class Resume {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("file_name")
    private String fileName;

    @TableField("file_url")
    private String fileUrl;

    @TableField("status")
    private ResumeStatus status;

    @TableField("raw_text")
    private String rawText;

    @TableField("parsed_json")
    private String parsedJson;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ---------- 新增字段 ----------
    @TableField("candidate_name")
    private String candidateName;

    @TableField("candidate_phone")
    private String candidatePhone;

    @TableField("candidate_email")
    private String candidateEmail;

    @TableField("skills")
    private String skills;  // 技能，逗号分隔

    @TableField("education_json")
    private String educationJson;  // 教育经历 JSON

    @TableField("work_json")
    private String workJson;  // 工作经历 JSON

    @TableField("expected_job")
    private String expectedJob;
}
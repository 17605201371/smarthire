package com.resume_ai_project.smarthire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 职位申请实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("applications")
public class Application {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long positionId;
    
    private Long resumeId;
    
    private Long userId;
    
    private String coverLetter;
    
    private String expectedSalary;
    
    private String availableDate;
    
    private String status; // APPLIED, VIEWED, INTERVIEW, REJECTED
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

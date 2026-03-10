package com.resume_ai_project.smarthire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeParseMessage implements Serializable {
    private Long resumeId;
    private String fileUrl;
}
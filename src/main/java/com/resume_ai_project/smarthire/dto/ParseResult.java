package com.resume_ai_project.smarthire.dto;

import lombok.Data;
import java.util.List;

@Data
public class ParseResult {
    private String name;
    private String phone;
    private String email;
    private List<String> skills;
    private List<Education> educationList;
    private List<WorkExperience> workExperienceList;
    private String summary; // 可扩展
    private String expectedJob; // 期望职位

    @Data
    public static class Education {
        private String school;
        private String degree;
        private String startDate;
        private String endDate;
    }

    @Data
    public static class WorkExperience {
        private String company;
        private String position;
        private String startDate;
        private String endDate;
        private String description;
    }
}
package com.resume_ai_project.smarthire.dto;

import com.resume_ai_project.smarthire.entity.Role;
import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String phone;
    private String password;
    private Role role; // CANDIDATE 或 EMPLOYER
}
package com.resume_ai_project.smarthire.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String phone;
    private String role;

    public LoginResponse(String token, Long id, String username, String phone, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.role = role;
    }
}
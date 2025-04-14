package com.project.TwitterClone.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupDto {
    private String email;
    private String password;
    private String fullName;
    private String createdAt;
}

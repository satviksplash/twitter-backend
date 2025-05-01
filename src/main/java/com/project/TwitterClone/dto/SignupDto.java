package com.project.TwitterClone.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupDto {
    @NotNull
    private String email;
    private String password;
    private String fullName;
    private String createdAt;
}

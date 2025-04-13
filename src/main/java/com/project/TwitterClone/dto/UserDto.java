package com.project.TwitterClone.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private  String fullName;
    private String email;
    private String location;
    private String coverImage;
    private String profileImage;
    private String bio;
    private String createdAt;
    private boolean req_user;
    private List<UserDto>followers = new ArrayList<>();
    private List<UserDto>following = new ArrayList<>();

    private boolean isFollowed;
}

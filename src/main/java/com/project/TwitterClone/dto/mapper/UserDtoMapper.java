package com.project.TwitterClone.dto.mapper;

import com.project.TwitterClone.dto.UserDto;
import com.project.TwitterClone.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDtoMapper {
    public static UserDto toUserDto(User user){
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());
        userDto.setProfileImage(user.getProfileImage());
        userDto.setCoverImage(user.getCoverImage());
        userDto.setBio(user.getBio());
        userDto.setCreatedAt(user.getCreatedAt());

        userDto.setFollowers(toUserDtos(user.getFollowers()));
        userDto.setFollowing(toUserDtos(user.getFollowings()));

        userDto.setLocation(user.getLocation());

        return userDto;
    }

    public static List<UserDto> toUserDtos(List<User> followers) {
        List<UserDto>userDtos = new ArrayList<>();
        for(User user : followers){
            UserDto userDto = new UserDto();

            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());
            userDto.setFullName(user.getFullName());
            userDto.setProfileImage(user.getProfileImage());
            userDto.setCoverImage(user.getCoverImage());
            userDto.setBio(user.getBio());
            userDto.setCreatedAt(user.getCreatedAt());

            userDtos.add(userDto);
        }
        return userDtos;
    }
}

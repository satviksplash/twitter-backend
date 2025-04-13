package com.project.TwitterClone.Controller;

import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Service.UserService;
import com.project.TwitterClone.dto.UserDto;
import com.project.TwitterClone.dto.mapper.UserDtoMapper;
import com.project.TwitterClone.model.User;
import com.project.TwitterClone.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/profile")
    public ResponseEntity<UserDto>getUserProfile(@RequestHeader("Authorization") String jwt) throws UserException{
        User user = userService.findUserProfileByJwt(jwt);

        UserDto userDto =  UserDtoMapper.toUserDto(user);
        userDto.setReq_user(true);

        return new ResponseEntity<>(userDto,HttpStatus.ACCEPTED);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserDto>getUserId(@PathVariable Long userId, @RequestHeader("Authorization") String jwt) throws UserException{
        User req_user = userService.findUserProfileByJwt(jwt);
        User user = userService.findUserById(userId);
        UserDto userDto =  UserDtoMapper.toUserDto(user);


        userDto.setReq_user(UserUtil.isReqUser(req_user, user));
        userDto.setFollowed(UserUtil.isFollowedByReqUser(req_user, user));

        return new ResponseEntity<>(userDto,HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>>searchUser(@RequestParam String query, @RequestHeader("Authorization") String jwt) throws UserException{
        User req_user = userService.findUserProfileByJwt(jwt);
        List<User> users = userService.SearchUser(query);
        List<UserDto> userDtos =  UserDtoMapper.toUserDtos(users);


//        userDto.setReq_user(UserUtil.isReqUser(req_user, user));
//        userDto.setFollowed(UserUtil.isFollowedByReqUser(req_user, user));

        return new ResponseEntity<>(userDtos,HttpStatus.ACCEPTED);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto>updateUser(@RequestBody User req, @RequestHeader("Authorization") String jwt) throws UserException{
        User user = userService.findUserProfileByJwt(jwt);
        User updatedUser = userService.updateUser(user.getId(), req);
        UserDto userDto =  UserDtoMapper.toUserDto(updatedUser);


//        userDto.setReq_user(UserUtil.isReqUser(req_user, user));
//        userDto.setFollowed(UserUtil.isFollowedByReqUser(req_user, user));

        return new ResponseEntity<>(userDto,HttpStatus.ACCEPTED);
    }

    @PutMapping("/{userId}/follow")
    public ResponseEntity<UserDto>followUser(@PathVariable Long userId, @RequestHeader("Authorization") String jwt) throws UserException{
        User user = userService.findUserProfileByJwt(jwt);

        User updatedUser = userService.followUser(userId, user);
        UserDto userDto =  UserDtoMapper.toUserDto(updatedUser);
//        userDto.setReq_user(UserUtil.isReqUser(req_user, user));
        userDto.setFollowed(UserUtil.isFollowedByReqUser(user, updatedUser));

        return new ResponseEntity<>(userDto,HttpStatus.ACCEPTED);
    }

}

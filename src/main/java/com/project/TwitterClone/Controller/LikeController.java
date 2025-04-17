package com.project.TwitterClone.Controller;

import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Service.LikeService;
import com.project.TwitterClone.Service.UserService;
import com.project.TwitterClone.dto.LikeDto;
import com.project.TwitterClone.dto.mapper.LikeDtoMapper;
import com.project.TwitterClone.model.Like;
import com.project.TwitterClone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LikeController {

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @PostMapping("/{tweetId}/like")
    public ResponseEntity<LikeDto>likeTweet(@PathVariable Long tweetId, @RequestHeader("Authorization") String jwt)
    throws UserException, TweetException {
        User user = userService.findUserProfileByJwt(jwt);
        Like like = likeService.likeTweet(tweetId, user);

        LikeDto likeDto = LikeDtoMapper.toLikeDto(like, user);

        return new ResponseEntity<LikeDto>(likeDto, HttpStatus.CREATED);
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<LikeDto>>getALlLikes(@PathVariable Long tweetId, @RequestHeader("Authorization") String jwt)
            throws UserException, TweetException {
        User user = userService.findUserProfileByJwt(jwt);
        List<Like> likes = likeService.getAllLikes(tweetId);
        List<LikeDto> likeDtos = LikeDtoMapper.toLikeDtos(likes,user);

        return new ResponseEntity<>(likeDtos, HttpStatus.OK);
    }

}

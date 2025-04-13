package com.project.TwitterClone.Controller;


import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Request.TweetReplyRequest;
import com.project.TwitterClone.Service.TweetService;
import com.project.TwitterClone.Service.UserService;
import com.project.TwitterClone.dto.TweetDto;
import com.project.TwitterClone.dto.mapper.TweetDtoMapper;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import com.project.TwitterClone.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tweets")
public class TweetController {
    @Autowired
    private TweetService tweetService;
    @Autowired
    private UserService userService;


    @PostMapping("/create")
    public ResponseEntity<TweetDto> createTweet(@RequestBody Tweet req, @RequestHeader("Authorization") String jwt)
            throws UserException, TweetException {

        User user = userService.findUserProfileByJwt(jwt);

        Tweet tweet = tweetService.createTweet(req, user);

        TweetDto tweetDto = TweetDtoMapper.toTweetDto(tweet, user);

        return new ResponseEntity<>(tweetDto, HttpStatus.CREATED);
    }

    @PostMapping("/reply")
    public ResponseEntity<TweetDto> createReply(@RequestBody TweetReplyRequest req, @RequestHeader("Authorization") String jwt)
            throws UserException, TweetException {

        User user = userService.findUserProfileByJwt(jwt);

        Tweet tweet = tweetService.createReply(req, user);

        TweetDto tweetDto = TweetDtoMapper.toTweetDto(tweet, user);

        return new ResponseEntity<>(tweetDto, HttpStatus.CREATED);
    }

    @PutMapping("/{tweetId}/retweet")
    public ResponseEntity<TweetDto> reTweet(@PathVariable Long tweetId, @RequestHeader("Authorization") String jwt)
            throws UserException, TweetException {

        User user = userService.findUserProfileByJwt(jwt);

        Tweet tweet = tweetService.reTweet(tweetId, user);

        TweetDto tweetDto = TweetDtoMapper.toTweetDto(tweet, user);

        return new ResponseEntity<>(tweetDto, HttpStatus.OK);
    }

    @GetMapping("/{tweetId}")
    public ResponseEntity<TweetDto> findTweetById(@PathVariable Long tweetId, @RequestHeader("Authorization") String jwt)
            throws UserException, TweetException {

        User user = userService.findUserProfileByJwt(jwt);

        Tweet tweet = tweetService.findById(tweetId);

        TweetDto tweetDto = TweetDtoMapper.toTweetDto(tweet, user);

        return new ResponseEntity<>(tweetDto, HttpStatus.OK);
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<ApiResponse> deleteTweet(@PathVariable Long tweetId, @RequestHeader("Authorization") String jwt)
            throws UserException, TweetException {

        User user = userService.findUserProfileByJwt(jwt);

        tweetService.deleteTweetById(tweetId, user.getId());

        ApiResponse res =  new ApiResponse("Tweet Deleted Successfully", true);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @GetMapping("/")
    public ResponseEntity<List<TweetDto>> getAllTweets(@RequestHeader("Authorization") String jwt)
            throws UserException {

        User user = userService.findUserProfileByJwt(jwt);

        List<Tweet> tweet = tweetService.findAllTweets();

        List<TweetDto> tweetDto = TweetDtoMapper.toTweetDtos(tweet, user);

        return new ResponseEntity<>(tweetDto, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TweetDto>> getUsersAllTweets(@PathVariable Long userId,@RequestHeader("Authorization") String jwt)
            throws UserException {

        User user = userService.findUserProfileByJwt(jwt);

        List<Tweet> tweet = tweetService.getUserTweets(user);

        List<TweetDto> tweetDto = TweetDtoMapper.toTweetDtos(tweet, user);

        return new ResponseEntity<>(tweetDto, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/likes")
    public ResponseEntity<List<TweetDto>> findTweetsLikedByUser(@PathVariable Long userId,@RequestHeader("Authorization") String jwt)
            throws UserException {

        User user = userService.findUserProfileByJwt(jwt);

        List<Tweet> tweet = tweetService.findTweetsLikedByUser(user);

        List<TweetDto> tweetDto = TweetDtoMapper.toTweetDtos(tweet, user);

        return new ResponseEntity<>(tweetDto, HttpStatus.OK);
    }

}

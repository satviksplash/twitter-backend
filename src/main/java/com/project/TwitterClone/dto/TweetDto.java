package com.project.TwitterClone.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TweetDto {

    private Long id;
    private String content;
    private String image;
    private UserDto user;

    private LocalDateTime createdAt;
    private int totalLikes;
    private int totalReplies;
    private int totalRetweets;

    private boolean isLiked;

    private boolean isRetweet;

    private List<Long> reTweetUserIds;
    private List<TweetDto>replyTweets;
}

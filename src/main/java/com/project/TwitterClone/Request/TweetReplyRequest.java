package com.project.TwitterClone.Request;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class TweetReplyRequest {

    private String content;
    private Long tweetId;
    private LocalDateTime createdAt;
    private String image;
}

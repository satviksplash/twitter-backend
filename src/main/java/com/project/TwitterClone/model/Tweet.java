package com.project.TwitterClone.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    private String content;
    private String image;

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL)
    private List<Like>likes = new ArrayList<>();

    @OneToMany
    private List<Tweet>replyTweets = new ArrayList<>(); //

    @ManyToMany
    private List<User>retweetUser = new ArrayList<>(); //

    @ManyToOne
    private Tweet replyTo; // reply_to

    private LocalDateTime createdAt;


    @Column(name = "is_reply")
    private boolean isReply;

    @Column(name = "is_tweet")
    private boolean isTweet; // if not a reply then a tweet
}

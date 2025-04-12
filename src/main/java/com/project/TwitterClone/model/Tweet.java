package com.project.TwitterClone.model;


import jakarta.persistence.*;
import lombok.Data;

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
    private List<Tweet>reply_tweets = new ArrayList<>();

    @ManyToMany
    private List<User>retweet_user = new ArrayList<>();

    @ManyToOne
    private Tweet reply_to;

    private boolean is_reply; // is this tweet a reply
    private boolean is_tweet; // if not a reply then a tweet
}

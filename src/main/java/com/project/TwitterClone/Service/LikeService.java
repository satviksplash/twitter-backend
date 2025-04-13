package com.project.TwitterClone.Service;

import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.model.Like;
import com.project.TwitterClone.model.User;

import java.util.List;

public interface LikeService {
    public Like likeTweet(Long tweedId, User user) throws TweetException;

    public List<Like> getAllLikes(Long tweetId) throws TweetException;
}

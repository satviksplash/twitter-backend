package com.project.TwitterClone.Service;

import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Request.TweetReplyRequest;
import com.project.TwitterClone.dto.TweetDto;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;

import java.util.List;

public interface TweetService {

    public Tweet createTweet(TweetDto req, User user) throws UserException;

    public List<TweetDto> findAllTweets(User user);
    public Tweet reTweet(Long tweetId, User user) throws UserException, TweetException;
    public Tweet findById(Long tweetId) throws TweetException;

    public void deleteTweetById(Long tweetId, Long userId) throws UserException, TweetException;

    public Tweet createReply(TweetReplyRequest req, User user) throws TweetException;

    public List<Tweet>getUserTweets(User user);

    List<Tweet> findTweetsLikedByUser(User user); // likes contain user

    public void invalidateUserTimelines();
}

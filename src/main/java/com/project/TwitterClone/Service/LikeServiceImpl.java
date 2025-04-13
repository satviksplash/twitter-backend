package com.project.TwitterClone.Service;

import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.Repository.LikeRepository;
import com.project.TwitterClone.Repository.TweetRepository;
import com.project.TwitterClone.model.Like;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeServiceImpl implements LikeService{

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetService tweetService;

    @Override
    public Like likeTweet(Long tweedId, User user) throws TweetException {
        Like isLikeExist = likeRepository.isLikeExist(user.getId(), tweedId);
        if(isLikeExist!=null){
            // unlike
            likeRepository.deleteById(isLikeExist.getId());
            return isLikeExist;
        }
        Tweet tweet = tweetService.findById(tweedId);
        Like like = new Like();
        like.setTweet(tweet);
        like.setUser(user);

        Like savedLike = likeRepository.save(like);
//        tweet.getLikes().add(like);
//        tweetRepository.save(tweet);
        return savedLike;
    }

    @Override
    public List<Like> getAllLikes(Long tweetId) throws TweetException {
        Tweet tweet = tweetService.findById(tweetId);
        return likeRepository.findLikesByTweetId(tweetId);
    }
}
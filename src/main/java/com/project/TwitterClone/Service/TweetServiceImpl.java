package com.project.TwitterClone.Service;

import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Repository.TweetRepository;
import com.project.TwitterClone.Request.TweetReplyRequest;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TweetServiceImpl implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Override
    public Tweet createTweet(Tweet req, User user) throws UserException {
       Tweet tweet = new Tweet();
       tweet.setContent(req.getContent());
       tweet.setImage(req.getImage());
       tweet.setCreatedAt(req.getCreatedAt());
       tweet.setUser(user);
       tweet.setReply(false);
       tweet.setTweet(true);

       return tweetRepository.save(tweet);
    }

    @Override
    public List<Tweet> findAllTweets() {

        return tweetRepository.findAllByIsTweetTrueOrderByCreatedAtDesc();
    }

    @Override
    public Tweet reTweet(Long tweetId, User user) throws UserException, TweetException {
        boolean modified;
        Tweet tweet = findById(tweetId);
        if(tweet.getRetweetUser().contains(user)){
            tweet.getRetweetUser().remove(user); // un - retweet
        }else{
            tweet.getRetweetUser().add(user);
        }
        return tweetRepository.save(tweet);
    }

    @Override
    public Tweet findById(Long tweetId) throws TweetException {
       return tweetRepository.findById(tweetId)
               .orElseThrow(()-> new TweetException("Tweet not found with id "+ tweetId));
    }

    @Override
    public void deleteTweetById(Long tweetId, Long userId) throws UserException, TweetException {
        Tweet tweet = findById(tweetId);
        if(!userId.equals(tweet.getUser().getId())){
            throw new UserException("Cannot delete another user's tweet");
        }
        tweetRepository.deleteById(tweet.getId());
    }


    @Override
    public Tweet createReply(TweetReplyRequest req, User user) throws TweetException {
        Tweet replyTo = findById(req.getTweetId()); // The tweet being replied to

        Tweet reply = new Tweet();
        reply.setContent(req.getContent());
        reply.setImage(req.getImage());
        reply.setCreatedAt(req.getCreatedAt());
        reply.setUser(user);
        reply.setReply(true);
        reply.setTweet(false);
        reply.setReplyTo(replyTo);

        Tweet savedReply = tweetRepository.save(reply);

        // Add this reply to the parent tweet's reply list
        replyTo.getReplyTweets().add(savedReply);

        tweetRepository.save(replyTo); // Save the updated parent tweet
        return replyTo;
    }

    @Override
    public List<Tweet> getUserTweets(User user) {
        return tweetRepository.findByRetweetUserContainingOrUser_IdAndIsTweetTrueOrderByCreatedAtDesc(user, user.getId());
    }

    @Override
    public List<Tweet> findTweetsLikedByUser(User user) {
        return tweetRepository.findByLikesUser_Id(user.getId());
    }
}

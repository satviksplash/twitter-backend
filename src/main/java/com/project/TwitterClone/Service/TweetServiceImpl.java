package com.project.TwitterClone.Service;

import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Repository.TweetRepository;
import com.project.TwitterClone.Request.TweetReplyRequest;
import com.project.TwitterClone.dto.TweetDto;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.project.TwitterClone.Exception.ErrorMessages.CANNOT_DELETE_TWEET;
import static com.project.TwitterClone.Exception.ErrorMessages.TWEET_NOT_FOUND;

@Service
public class TweetServiceImpl implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Override
    public Tweet createTweet(TweetDto req, User user) throws UserException {
        Tweet tweet = Tweet.builder()
                .content(req.getContent())
                .image(req.getImage())
                .createdAt(req.getCreatedAt())
                .user(user)
                .isReply(false)
                .isTweet(true)
                .build();
       return tweetRepository.save(tweet);
    }

    @Override
    public List<Tweet> findAllTweets(User user) {

        List<Tweet> tweets = tweetRepository.findAllByIsTweetTrueOrderByCreatedAtDesc();
        Set<Long> followingIds = user.getFollowings()
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        return tweets.stream()
                .sorted(Comparator
                        .comparing((Tweet t) -> !followingIds.contains(t.getUser().getId()))
                        .thenComparing(Tweet::getCreatedAt, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public Tweet reTweet(Long tweetId, User user) throws UserException, TweetException {
        boolean modified;
        Tweet tweet = findById(tweetId);
        if(tweet.getRetweetUser().contains(user)){
//            tweet.setUser(user);
            tweet.getRetweetUser().remove(user); // un - retweet
        }else{
            tweet.getRetweetUser().add(user);
        }
        return tweetRepository.save(tweet);
    }

    @Override
    public Tweet findById(Long tweetId) throws TweetException {
       return tweetRepository.findById(tweetId)
               .orElseThrow(()-> new TweetException(TWEET_NOT_FOUND+ tweetId));
    }

    @Override
    public void deleteTweetById(Long tweetId, Long userId) throws UserException, TweetException {
        Tweet tweet = findById(tweetId);
        if(!userId.equals(tweet.getUser().getId())){
            throw new TweetException(CANNOT_DELETE_TWEET);
        }
        tweetRepository.deleteById(tweet.getId());
    }


    @Override
    public Tweet createReply(TweetReplyRequest req, User user) throws TweetException {
        Tweet replyTo = findById(req.getTweetId()); // The tweet being replied to

        Tweet reply = Tweet.builder()
                .content(req.getContent())
                .image(req.getImage())
                .createdAt(LocalDateTime.now())
                .user(user)
                .isReply(true)
                .isTweet(false)
                .replyTo(replyTo)
                .build();

        Tweet savedReply = tweetRepository.save(reply);

        // Add this reply to the parent tweet's reply list
        replyTo.getReplyTweets().add(savedReply);

        tweetRepository.save(replyTo); // Save the updated parent tweet
        return replyTo;
    }

    @Override
    public List<Tweet> getUserTweets(User user) {
        return tweetRepository.findByRetweetUserContainsOrUser_IdAndIsTweetTrueOrderByCreatedAtDesc(user, user.getId());
    }

    @Override
    public List<Tweet> findTweetsLikedByUser(User user) {
        return tweetRepository.findByLikesUser_Id(user.getId());
    }
}

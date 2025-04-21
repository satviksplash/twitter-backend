package com.project.TwitterClone.Service;

import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Repository.TweetRepository;
import com.project.TwitterClone.Request.TweetReplyRequest;
import com.project.TwitterClone.dto.TweetDto;
import com.project.TwitterClone.dto.mapper.TweetDtoMapper;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.project.TwitterClone.Exception.ErrorMessages.CANNOT_DELETE_TWEET;
import static com.project.TwitterClone.Exception.ErrorMessages.TWEET_NOT_FOUND;

@Service
@Slf4j
public class TweetServiceImpl implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Tweet createTweet(TweetDto req, User user) throws UserException {
        // Invalidate the cache for the user when a new tweet is created
        invalidateUserTimelines();
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

//    @Override
//    @Transactional
//    public List<Tweet> findAllTweets(User user) {
//
//        String cacheKey = "tweets:user:" + user.getId();
//
//        try{
//            @SuppressWarnings("unchecked")
//            List<Tweet> cachedTweets = (List<Tweet>) redisTemplate.opsForValue().get(cacheKey);
//
//            if (cachedTweets != null) {
//                log.info("Retrieved tweets from cache for user: {}", user.getId());
//                return cachedTweets;
//            }
//            log.info("not found in cache for user: {}", user.getId());
//
//        } catch (Exception e) {
//            log.info("Error retrieving tweets from cache: {}", e.getMessage());
//        }
//        // If cache is empty or an error occurred, fetch from the database
//        List<Tweet> tweets = tweetRepository.findAllByIsTweetTrueOrderByCreatedAtDesc();
//        Set<Long> followingIds = user.getFollowings()
//                .stream()
//                .map(User::getId)
//                .collect(Collectors.toSet());
//
//        List<Tweet> sortedTweets =  tweets.stream()
//                .sorted(Comparator
//                        .comparing((Tweet t) -> !followingIds.contains(t.getUser().getId()))
//                        .thenComparing(Tweet::getCreatedAt, Comparator.reverseOrder()))
//                .collect(Collectors.toList());
//
//
//        // Store the sorted tweets in Redis cache
//        redisTemplate.opsForValue().set(cacheKey, sortedTweets, 5, TimeUnit.MINUTES);
//
//        return sortedTweets;
//    }
    @Override
    @Transactional(readOnly = true)
    public List<TweetDto> findAllTweets(User user) {
        String cacheKey = "tweets:user:" + user.getId();

        try {
            @SuppressWarnings("unchecked")
            List<TweetDto> cachedDtos = (List<TweetDto>) redisTemplate.opsForValue().get(cacheKey);

            if (cachedDtos != null) {
                log.info("Retrieved tweets from cache for user: {}", user.getId());
                return cachedDtos;
            }
            log.info("Cache miss for user: {}", user.getId());

        } catch (Exception e) {
            log.info("Error reading from cache: {}", e.getMessage());
        }

        List<Tweet> tweets = tweetRepository.findAllByIsTweetTrueOrderByCreatedAtDesc();

        Set<Long> followingIds = user.getFollowings()
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        List<Tweet> sortedTweets = tweets.stream()
                .sorted(Comparator
                        .comparing((Tweet t) -> !followingIds.contains(t.getUser().getId()))
                        .thenComparing(Tweet::getCreatedAt, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        List<TweetDto> tweetDtos = TweetDtoMapper.toTweetDtos(sortedTweets, user);

        redisTemplate.opsForValue().set(cacheKey, tweetDtos, 5, TimeUnit.MINUTES);

        return tweetDtos;
    }

    @Override
    public Tweet reTweet(Long tweetId, User user) throws UserException, TweetException {
        boolean modified;
        Tweet tweet = findById(tweetId);
        // Invalidate the cache for the user when a tweet is retweeted
        invalidateUserTimelines();
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
        // Invalidate the cache for the user when a tweet is deleted
        invalidateUserTimelines();
        Tweet tweet = findById(tweetId);
        if(!userId.equals(tweet.getUser().getId())){
            throw new TweetException(CANNOT_DELETE_TWEET);
        }
        if (tweet.getReplyTo() != null) {
            tweet.getReplyTo().removeReply(tweet);
        }
        tweetRepository.delete(tweet);
    }


    @Transactional
    @Override
    public Tweet createReply(TweetReplyRequest req, User user) throws TweetException {
        // Invalidate the cache for the user when a reply is created
        invalidateUserTimelines();
        Tweet replyTo = findById(req.getTweetId()); // The tweet being replied to

        Tweet reply = Tweet.builder()
                .content(req.getContent())
                .image(req.getImage())
                .createdAt(LocalDateTime.now())
                .user(user)
                .isReply(true)
                .isTweet(false)
                .build();
        replyTo.addReply(reply);

        Tweet savedReply = tweetRepository.save(reply);



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

    public void invalidateUserTimelines() {
        Set<String> keys = redisTemplate.keys("tweets:user:*");
        redisTemplate.delete(keys);
    }
}

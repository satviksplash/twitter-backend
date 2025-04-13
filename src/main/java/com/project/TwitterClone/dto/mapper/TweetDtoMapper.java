package com.project.TwitterClone.dto.mapper;

import com.project.TwitterClone.dto.TweetDto;
import com.project.TwitterClone.dto.UserDto;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import com.project.TwitterClone.util.TweetUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TweetDtoMapper {


    public static TweetDto toTweetDto(Tweet tweet, User reqUser) {
        UserDto user = UserDtoMapper.toUserDto(tweet.getUser());
        boolean isLiked = TweetUtil.isLikedByReqUser(reqUser, tweet);

        boolean isRetweeted = TweetUtil.isRetweetedByReqUser(reqUser, tweet) ;

        List<Long>retweetUserIds = new ArrayList<>();

        for(User user1 : tweet.getRetweetUser()){
            retweetUserIds.add(user1.getId());
        }
        TweetDto tweetDto = new TweetDto();
        tweetDto.setId(tweet.getId());
        tweetDto.setContent(tweet.getContent());
        tweetDto.setImage(tweet.getImage());
        tweetDto.setCreatedAt(tweet.getCreatedAt());
        tweetDto.setTotalLikes(tweet.getLikes().size());
        tweetDto.setTotalReplies(tweet.getReplyTweets().size());

        tweetDto.setTotalRetweets(tweet.getRetweetUser().size());
        tweetDto.setUser(user);
        tweetDto.setLiked(isLiked);
        tweetDto.setRetweet(isRetweeted);
        tweetDto.setReTweetUserIds(retweetUserIds);

        tweetDto.setReplyTweets(toTweetDtos(tweet.getReplyTweets(), reqUser));
        return tweetDto;
    }

    public static List<TweetDto>toTweetDtos(List<Tweet>tweets, User reqUser){
        List<TweetDto> tweetDtos = new ArrayList<>();
        for(Tweet tweet : tweets){
            TweetDto tweetDto = toReplyTweetDtos(tweet, reqUser);
            tweetDtos.add(tweetDto);
        }
        return tweetDtos;
    }

    private static TweetDto toReplyTweetDtos(Tweet tweet, User reqUser) {
        UserDto user = UserDtoMapper.toUserDto(tweet.getUser());
        boolean isLiked = TweetUtil.isLikedByReqUser(reqUser, tweet);

        boolean isRetweeted = TweetUtil.isRetweetedByReqUser(reqUser, tweet) ;

        List<Long>retweetUserIds = new ArrayList<>();

        for(User user1 : tweet.getRetweetUser()){
            retweetUserIds.add(user1.getId());
        }
        TweetDto tweetDto = new TweetDto();
        tweetDto.setId(tweet.getId());
        tweetDto.setContent(tweet.getContent());
        tweetDto.setImage(tweet.getImage());
        tweetDto.setCreatedAt(tweet.getCreatedAt());
        tweetDto.setTotalLikes(tweet.getLikes().size());
        tweetDto.setTotalReplies(tweet.getReplyTweets().size());

        tweetDto.setTotalRetweets(tweet.getRetweetUser().size());
        tweetDto.setUser(user);
        tweetDto.setLiked(isLiked);
        tweetDto.setRetweet(isRetweeted);
        tweetDto.setReTweetUserIds(retweetUserIds);


        return tweetDto;
    }
}


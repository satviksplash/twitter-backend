package com.project.TwitterClone.dto.mapper;

import com.project.TwitterClone.dto.LikeDto;
import com.project.TwitterClone.dto.TweetDto;
import com.project.TwitterClone.dto.UserDto;
import com.project.TwitterClone.model.Like;
import com.project.TwitterClone.model.User;

import java.util.ArrayList;
import java.util.List;

public class LikeDtoMapper {

    public static LikeDto toLikeDto(Like like, User reqUser){
        UserDto user = UserDtoMapper.toUserDto(like.getUser());
        UserDto reqUserDto = UserDtoMapper.toUserDto(reqUser);
        TweetDto tweetDto = TweetDtoMapper.toTweetDto(like.getTweet(), reqUser);


        LikeDto likeDto = new LikeDto();
        likeDto.setId(like.getId());
        likeDto.setTweet(tweetDto);
        likeDto.setUser(user);

        return likeDto;
    }

    public static List<LikeDto> toLikeDtos(List<Like>likes, User reqUser){
        List<LikeDto>likeDtos = new ArrayList<>();

        for(Like like : likes){
            UserDto user = UserDtoMapper.toUserDto(like.getUser());
            TweetDto tweetDto = TweetDtoMapper.toTweetDto(like.getTweet(), reqUser);
            LikeDto likeDto = new LikeDto();
            likeDto.setId(like.getId());
            likeDto.setTweet(tweetDto);
            likeDto.setUser(user);

            likeDtos.add(likeDto);
        }

        return likeDtos;
    }
}

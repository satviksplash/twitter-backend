package com.project.TwitterClone.util;

import com.project.TwitterClone.model.Like;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;

public class TweetUtil {
    public static final boolean isLikedByReqUser(User reqUser, Tweet tweet){

        for(Like like : tweet.getLikes()){
            if(like.getUser().getId().equals(reqUser.getId())){
                return true;
            }
        }
        return false;

    }

    public static final boolean isRetweetedByReqUser(User reqUser, Tweet tweet){
        for(User user : tweet.getRetweetUser()){
            if(user.getId().equals(reqUser.getId())) return true;
        }
        return false;
    }
}

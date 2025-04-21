package com.project.TwitterClone.Repository;

import com.project.TwitterClone.model.Like;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TweetRepository tweetRepository;

    private User user;
    private Tweet tweet;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .fullName("Example User")
                .email("example@example.com")
                .password("Password@123")
                .createdAt("April 8, 1999")
                .build());

        tweet = tweetRepository.save(Tweet.builder()
                .user(user)
                .content("Hello, world!")
                .isTweet(true)
                .isReply(false)
                .createdAt(LocalDateTime.now())
                .build());

        likeRepository.save(Like.builder()
                .user(user)
                .tweet(tweet)
                .build());
    }

    @Test
    void isLikeExist_shouldReturnLike() {
        Like like = likeRepository.isLikeExist(user.getId(), tweet.getId());
        assertThat(like).isNotNull();
        assertThat(like.getUser().getId()).isEqualTo(user.getId());
        assertThat(like.getTweet().getId()).isEqualTo(tweet.getId());
    }

    @Test
    void findLikesByTweetId_shouldReturnListOfLikes() {
        List<Like> likes = likeRepository.findLikesByTweetId(tweet.getId());
        assertThat(likes).hasSize(1);
        assertThat(likes.get(0).getUser().getEmail()).isEqualTo(user.getEmail());
    }
}
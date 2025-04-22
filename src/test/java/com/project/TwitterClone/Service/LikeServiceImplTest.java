package com.project.TwitterClone.Service;

import com.project.TwitterClone.Exception.TweetException;
import com.project.TwitterClone.Repository.LikeRepository;
import com.project.TwitterClone.Repository.TweetRepository;
import com.project.TwitterClone.model.Like;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private TweetRepository tweetRepository;
    @Mock
    private TweetService tweetService;
    @InjectMocks
    private LikeServiceImpl likeService;
    private User user;
    private Tweet tweet;
    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("alice@example.com")
                .fullName("Alice Wonderland")
                .password("Password@123")
                .createdAt("April 19, 2001")
                .build();

        tweet = Tweet.builder()
                .content("This is test tweet 1")
                .user(user)
                .isTweet(true)
                .isReply(false)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
    }

    @Test
    void likeTweet_whenNotLikedYet_shouldCreateLike() throws TweetException {
        when(likeRepository.isLikeExist(user.getId(), tweet.getId())).thenReturn(null);
        when(tweetService.findById(tweet.getId())).thenReturn(tweet);

        Like expectedLike = Like.builder().id(10L).tweet(tweet).user(user).build();
        when(likeRepository.save(any(Like.class))).thenReturn(expectedLike);

        // Act
        Like actualLike = likeService.likeTweet(tweet.getId(), user);

        // Assert
        assertNotNull(actualLike);
        assertEquals(expectedLike.getId(), actualLike.getId());
        verify(likeRepository).save(any(Like.class));
        verify(tweetService).invalidateUserTimelines();
    }

    @Test
    void likeTweet_whenAlreadyLiked_shouldUnlike() throws TweetException {
        Like existingLike = Like.builder().id(99L).tweet(tweet).user(user).build();
        when(likeRepository.isLikeExist(user.getId(), tweet.getId())).thenReturn(existingLike);

        Like result = likeService.likeTweet(tweet.getId(), user);

        assertEquals(existingLike.getId(), result.getId());
        verify(likeRepository).deleteById(existingLike.getId());
        verify(likeRepository, never()).save(any());
        verify(tweetService).invalidateUserTimelines();
    }

    @Test
    void getAllLikes_shouldReturnListOfLikes() throws TweetException {
        // Arrange
        when(tweetService.findById(tweet.getId())).thenReturn(tweet);

        List<Like> likeList = List.of(
                Like.builder().id(1L).user(user).tweet(tweet).build()
        );
        when(likeRepository.findLikesByTweetId(tweet.getId())).thenReturn(likeList);

        // Act
        List<Like> result = likeService.getAllLikes(tweet.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getUser().getId());
    }
}
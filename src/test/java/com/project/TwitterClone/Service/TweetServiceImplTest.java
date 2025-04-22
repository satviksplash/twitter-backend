package com.project.TwitterClone.Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Repository.TweetRepository;
import com.project.TwitterClone.dto.TweetDto;
import com.project.TwitterClone.dto.UserDto;
import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TweetServiceImplTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TweetServiceImpl tweetService;

    private User user;
    private Tweet tweet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");

        tweet = new Tweet();
        tweet.setId(100L);
        tweet.setContent("Test tweet");
        tweet.setCreatedAt(LocalDateTime.now());
        tweet.setUser(user);
        tweet.setTweet(true);

        user.setFollowings(Collections.emptyList());
    }

    @Test
    void findAllTweets_whenCacheMiss_shouldFetchFromDbAndCache() {
        String cacheKey = "tweets:user:" + user.getId();

        // cache miss
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);

        // repository call
        when(tweetRepository.findAllByIsTweetTrueOrderByCreatedAtDesc())
                .thenReturn(Collections.singletonList(tweet));

        // run method
        List<TweetDto> result = tweetService.findAllTweets(user);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(tweet.getId(), result.get(0).getId());
        verify(redisTemplate, times(2)).opsForValue();
        verify(valueOperations).set(eq(cacheKey), any(), eq(5L), eq(TimeUnit.MINUTES));
    }
    @Test
    void findAllTweets_whenCacheHit_shouldReturnCachedData() {
        // Arrange
        String cacheKey = "tweets:user:" + user.getId();

        TweetDto cachedTweetDto = new TweetDto();
        cachedTweetDto.setId(101L);
        cachedTweetDto.setContent("Cached tweet");
        cachedTweetDto.setUser(new UserDto());
        List<TweetDto> cachedTweetDtos = Collections.singletonList(cachedTweetDto);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(cachedTweetDtos);

        List<TweetDto> result = tweetService.findAllTweets(user);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cachedTweetDto.getId(), result.get(0).getId());

        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(cacheKey);
        verifyNoInteractions(tweetRepository);
    }

    @Test
    void createTweet_shouldBuildAndSaveTweetSuccessfully() throws UserException {
        // Arrange
        TweetDto req = new TweetDto();
        req.setContent("Hello Twitter!");
        req.setImage("https://example.com/image.jpg");
        req.setCreatedAt(LocalDateTime.now());

        user.setId(1L);

        ArgumentCaptor<Tweet> tweetCaptor = ArgumentCaptor.forClass(Tweet.class);

        when(tweetRepository.save(any(Tweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tweet savedTweet = tweetService.createTweet(req, user);

        assertNotNull(savedTweet);
        assertEquals(req.getContent(), savedTweet.getContent());
        assertEquals(req.getImage(), savedTweet.getImage());
        assertEquals(user, savedTweet.getUser());
        assertTrue(savedTweet.isTweet());
        assertFalse(savedTweet.isReply());

        verify(tweetRepository).save(tweetCaptor.capture());
        Tweet capturedTweet = tweetCaptor.getValue();
        assertEquals(req.getContent(), capturedTweet.getContent());

        verify(redisTemplate).delete(anySet());
    }


}

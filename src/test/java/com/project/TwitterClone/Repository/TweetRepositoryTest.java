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
class TweetRepositoryTest {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Tweet tweet1;
    private Tweet tweet2;
    private Tweet reply1;
    private Tweet tweet3;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .fullName("Test User One")
                .email("user1@test.com")
                .password("Test@123")
                .username("testuser1")
                .build();

        user2 = User.builder()
                .fullName("Test User Two")
                .email("user2@test.com")
                .password("Test@123")
                .username("testuser2")
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        tweet1 = Tweet.builder()
                .content("This is test tweet 1")
                .user(user1)
                .isTweet(true)
                .isReply(false)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        tweet2 = Tweet.builder()
                .content("This is test tweet 2")
                .user(user2)
                .isTweet(true)
                .isReply(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        reply1 = Tweet.builder()
                .content("This is a reply to tweet 1")
                .user(user2)
                .isTweet(false)
                .isReply(true)
                .replyTo(tweet1)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        tweet3 = Tweet.builder()
                .content("This is test tweet 3")
                .user(user1)
                .isTweet(true)
                .isReply(false)
                .createdAt(LocalDateTime.now())
                .build();

        tweet1 = tweetRepository.save(tweet1);
        tweet2 = tweetRepository.save(tweet2);
        reply1 = tweetRepository.save(reply1);
        tweet3 = tweetRepository.save(tweet3);

        tweet1.getRetweetUser().add(user2);
        tweet1 = tweetRepository.save(tweet1);

        Like like1 = Like.builder()
                .user(user2)
                .tweet(tweet1)
                .build();

        Like like2 = Like.builder()
                .user(user1)
                .tweet(tweet2)
                .build();

        // Need to inject LikeRepository if we're going to save likes directly
        // Otherwise we can just add them to the tweet's likes collection
        tweet1.getLikes().add(like1);
        tweet2.getLikes().add(like2);

        tweetRepository.save(tweet1);
        tweetRepository.save(tweet2);
    }

    @Test
    void findAllByIsTweetTrueOrderByCreatedAtDesc() {
        // Execute
        List<Tweet> result = tweetRepository.findAllByIsTweetTrueOrderByCreatedAtDesc();

        // Verify
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        // Should be ordered by createdAt desc (newest first)
        assertThat(result.get(0).getId()).isEqualTo(tweet3.getId());
        assertThat(result.get(1).getId()).isEqualTo(tweet2.getId());
        assertThat(result.get(2).getId()).isEqualTo(tweet1.getId());

        // Verify all returned items are tweets, not replies
        assertThat(result).allMatch(Tweet::isTweet);
        assertThat(result).doesNotContain(reply1);
    }

    @Test
    void findByRetweetUserContainsOrUser_IdAndIsTweetTrueOrderByCreatedAtDesc() {
        // Execute - find tweets retweeted by user2 OR created by user1 that are tweets
        List<Tweet> result = tweetRepository.findByRetweetUserContainsOrUser_IdAndIsTweetTrueOrderByCreatedAtDesc(user2, user1.getId());

        assertThat(result).isNotNull();

        // Should include tweet1 (retweeted by user2) and user1's tweets (tweet1, tweet3)
        assertThat(result).contains(tweet1, tweet3);

        // Should be ordered by createdAt desc
        assertThat(result.get(0).getId()).isEqualTo(tweet3.getId());

        assertThat(result).doesNotContain(reply1);
    }

    @Test
    void findByLikesUser_Id() {
        List<Tweet> result = tweetRepository.findByLikesUser_Id(user2.getId());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(tweet1.getId());

        List<Tweet> result2 = tweetRepository.findByLikesUser_Id(user1.getId());

        assertThat(result2).isNotNull();
        assertThat(result2).hasSize(1);
        assertThat(result2.get(0).getId()).isEqualTo(tweet2.getId());
    }
}
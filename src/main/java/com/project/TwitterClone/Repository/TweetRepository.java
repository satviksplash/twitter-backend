package com.project.TwitterClone.Repository;

import com.project.TwitterClone.model.Tweet;
import com.project.TwitterClone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    public List<Tweet> findAllByIsTweetTrueOrderByCreatedAtDesc();
    List<Tweet> findByRetweetUserContainingOrUser_IdAndIsTweetTrueOrderByCreatedAtDesc(User user, Long userId);

    List<Tweet> findByLikesContainingOrderByCreatedAtDesc(User user);

    @Query("Select t from Tweet t JOIN t.likes l where l.user.id =:userId")
    List<Tweet>findByLikesUser_Id(Long userId);
}

package com.project.TwitterClone.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tweet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne
    @ToString.Exclude
    private User user;

    @Size(max = 250, message = "Content cannot exceed 250 characters")
    @Column(length = 250)
    private String content;

    @URL(message = "Please provide a valid URL for image")
    private String image;

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<Like>likes = new ArrayList<>();

    @OneToMany(mappedBy = "replyTo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Tweet>replyTweets = new ArrayList<>(); //

    @ManyToMany
    @Builder.Default
    private List<User>retweetUser = new ArrayList<>(); //

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    private Tweet replyTo; // reply_to

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;


    @Column(name = "is_reply")
    private boolean isReply;

    @Column(name = "is_tweet")
    private boolean isTweet; // if not a reply then a tweet

    public void addReply(Tweet reply) {
        replyTweets.add(reply);
        reply.setReplyTo(this);
    }

    public void removeReply(Tweet reply) {
        replyTweets.remove(reply);
        reply.setReplyTo(null);
    }
}

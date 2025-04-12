package com.project.TwitterClone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fullName;  //
    private String location;
    private String createdAt; //
//    private String
    private String email;
    private String password;

    private String profileImage; //
    private String coverImage; //
    private String bio;
    private String username;
//    private boolean login_with_google; // in future to implement oAuth

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Tweet>tweet= new ArrayList<>(); // one user can have many tweets

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>(); // one user can like many tweets

    @JsonIgnore
    @ManyToMany
    private List<User>followers = new ArrayList<>(); // every user can have many followers

    @JsonIgnore
    @ManyToMany
    private List<User>followings = new ArrayList<>(); // every user can follow many users





}

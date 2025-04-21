package com.project.TwitterClone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Length(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Full name can only contain letters and spaces")
    private String fullName;  //
    @Length(max = 50, message = "Location cannot exceed 50 characters")
    private String location;
    @Column(updatable = false)
    private String createdAt; //
//    private String

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(unique = true)
    private String email;


    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least 8 characters, one uppercase, one lowercase, one number and one special character")
    private String password;

    @URL(message = "Please provide a valid URL for profile image")
    private String profileImage; //
    @URL(message = "Please provide a valid URL for profile image")
    private String coverImage; //

    @Length(max = 160, message = "Bio cannot exceed 160 characters")
    private String bio;
    private String username;
//    private boolean login_with_google; // in future to implement oAuth

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Tweet>tweet= new ArrayList<>(); // one user can have many tweets

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Like> likes = new ArrayList<>(); // one user can like many tweets

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany
    private List<User>followers = new ArrayList<>(); // every user can have many followers

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany
    private List<User>followings = new ArrayList<>(); // every user can follow many users
}

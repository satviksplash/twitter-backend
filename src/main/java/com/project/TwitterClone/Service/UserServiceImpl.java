package com.project.TwitterClone.Service;

import com.project.TwitterClone.Config.JwtProvider;
import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Repository.UserRepository;
import com.project.TwitterClone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public User findUserById(Long userId) throws UserException {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserException("user not found with id " + userId));

        return  user;
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {
        String email  = jwtProvider.getEmailFromToken(jwt);
        User user  = userRepository.findByEmail(email);
        if(user == null)  throw new UserException("User not found with  email "+ email);
        return user;
    }

    @Override
    public User updateUser(Long userId, User req) throws UserException {
        User user = findUserById(userId);
        if(req.getFullName()!=null)  user.setFullName(req.getFullName());

        if(req.getLocation()!=null) user.setLocation(req.getLocation());

        if(req.getBio()!=null) user.setBio(req.getBio());
        if(req.getProfileImage()!=null) user.setProfileImage(req.getProfileImage());
        if(req.getCoverImage()!=null) user.setCoverImage(req.getCoverImage());
//        if(req.getCreatedAt() != null) user.setCreatedAt(req.getCreatedAt()); // created at is not changable

        return userRepository.save(user);
    }

    @Override
    public User followUser(Long userId, User user) throws UserException {
        User followToUser = findUserById(userId);

        if(user.getFollowings().contains(followToUser) && followToUser.getFollowers().contains(user)){
            //user already follow followToUser
            // we need to toggle to unfollow
            user.getFollowings().remove(followToUser);
            followToUser.getFollowers().remove(user);
        }else{
            user.getFollowings().add(followToUser);
            followToUser.getFollowers().add(user);
        }

        userRepository.save(followToUser);
        userRepository.save(user);

        return followToUser;

    }

    @Override
    public List<User> SearchUser(String query) {
        return userRepository.searchUser(query);
    }
}

package com.project.TwitterClone.Service;

import com.project.TwitterClone.Config.JwtProvider;
import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Repository.UserRepository;
import com.project.TwitterClone.dto.LoginDto;
import com.project.TwitterClone.dto.SignupDto;
import com.project.TwitterClone.model.User;
import com.project.TwitterClone.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private CustomUserDetailsServiceImplementation customUserDetailsServiceImpl;
    @Override
    public AuthResponse signup(SignupDto user) throws UserException {
        String email = user.getEmail();
        String password = user.getPassword();

        String full_name = user.getFullName();
        String created_at = user.getCreatedAt();

        User isEmailExist = userRepository.findByEmail(email);

        if(isEmailExist!=null){
            throw new UserException("Email already used for another account");
        }
        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setCreatedAt(created_at);
        createdUser.setFullName(full_name);

        User savedUser = userRepository.save(createdUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);

        return new AuthResponse(token, true);
    }
    public AuthResponse signIn(LoginDto user) throws UserException{
        String username = user.getEmail();
        String password = user.getPassword();
        Authentication authentication = authenticate(username, password);
        String token = jwtProvider.generateToken(authentication);

        return new AuthResponse(token, true);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetailsServiceImpl.loadUserByUsername(username);
        if(userDetails == null){
            throw new BadCredentialsException("Invalid username");
        }

        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("Invalid email or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

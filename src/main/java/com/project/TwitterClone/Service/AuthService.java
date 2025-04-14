package com.project.TwitterClone.Service;


import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.dto.LoginDto;
import com.project.TwitterClone.dto.SignupDto;
import com.project.TwitterClone.response.AuthResponse;

public interface AuthService {
    public AuthResponse signup(SignupDto user)  throws UserException;
    public AuthResponse signIn(LoginDto user) throws UserException;
}

package com.project.TwitterClone.Controller;

import com.project.TwitterClone.Exception.UserException;
import com.project.TwitterClone.Service.AuthService;
import com.project.TwitterClone.dto.LoginDto;
import com.project.TwitterClone.dto.SignupDto;

import com.project.TwitterClone.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody SignupDto user) throws UserException {
        AuthResponse authResponse = authService.signup(user);
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse>signIn(@RequestBody LoginDto user) throws UserException {
        AuthResponse authResponse = authService.signIn(user);
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.ACCEPTED);
    }
}

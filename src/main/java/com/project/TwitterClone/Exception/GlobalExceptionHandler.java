package com.project.TwitterClone.Exception;


import com.project.TwitterClone.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("already used")) {
            status = HttpStatus.CONFLICT;
        }

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), status.value());
        return ResponseEntity.status(status).body(errorResponse);

    }

    @ExceptionHandler(TweetException.class)
    public ResponseEntity<ErrorResponse> handleTweetException(TweetException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("Cannot delete")) {
            status = HttpStatus.FORBIDDEN;
        }

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), status.value());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(BadCredentialsException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), status.value());
        return ResponseEntity.status(status).body(errorResponse);
    }
}

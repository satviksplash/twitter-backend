package com.project.TwitterClone.Exception;


import com.project.TwitterClone.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable cause = ex.getRootCause();

        if (cause instanceof ConstraintViolationException) {
            Map<String, String> errors = new HashMap<>();
            ((ConstraintViolationException) cause).getConstraintViolations()
                    .forEach(violation -> {
                        String field = violation.getPropertyPath().toString();
                        String message = violation.getMessage();
                        errors.put(field, message);
                    });

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                Collections.singletonMap("error", "Data validation failed"),
                HttpStatus.BAD_REQUEST
        );
    }
}

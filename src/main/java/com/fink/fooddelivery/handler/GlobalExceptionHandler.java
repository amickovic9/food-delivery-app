package com.fink.fooddelivery.handler;

import com.fink.fooddelivery.exception.AuthException;
import com.fink.fooddelivery.exception.CustomExceptionMessage;
import com.fink.fooddelivery.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<CustomExceptionMessage> handleUserAlreadyExistsException(UserAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new CustomExceptionMessage(ex.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<CustomExceptionMessage> handleUserAlreadyExistsException(AuthException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CustomExceptionMessage(ex.getMessage()));
    }
}

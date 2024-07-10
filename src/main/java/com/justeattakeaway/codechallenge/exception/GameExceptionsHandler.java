package com.justeattakeaway.codechallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GameExceptionsHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(IllegalStateException ex) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_ACCEPTABLE);
    }

}

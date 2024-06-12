package com.otunba.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = {ApiException.class})
    public ResponseEntity<Object> handleApiException(ApiException exception){
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return ResponseEntity.badRequest().body(exceptionResponse);
    }
}

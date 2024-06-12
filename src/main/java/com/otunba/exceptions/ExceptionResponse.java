package com.otunba.exceptions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
public class ExceptionResponse {
    private final String message;
    private final HttpStatus status;
    private final ZonedDateTime time;
}

package com.ndd.simi_be.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AppException{
    public InvalidTokenException(String message){
        super(HttpStatus.UNAUTHORIZED, message);
    }
}

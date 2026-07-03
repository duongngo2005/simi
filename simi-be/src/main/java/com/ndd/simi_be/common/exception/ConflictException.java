package com.ndd.simi_be.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends AppException{
    public ConflictException(String message){
        super(HttpStatus.CONFLICT, message);
    }
}

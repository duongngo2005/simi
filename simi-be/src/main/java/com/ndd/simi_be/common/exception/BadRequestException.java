package com.ndd.simi_be.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AppException{
    public BadRequestException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }
}

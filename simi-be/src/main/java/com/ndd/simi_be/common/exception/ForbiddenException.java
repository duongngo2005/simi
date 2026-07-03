package com.ndd.simi_be.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppException{
    public ForbiddenException(String message){
        super(HttpStatus.FORBIDDEN, message);
    }
}

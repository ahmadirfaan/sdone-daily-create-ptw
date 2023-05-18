package com.sdone.createdailyptw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GrpcClientException extends ResponseStatusException {

    public GrpcClientException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}

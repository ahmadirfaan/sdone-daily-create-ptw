package com.sdone.createdailyptw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidDataPtwException extends ResponseStatusException {

    public InvalidDataPtwException() {
        super(HttpStatus.BAD_REQUEST, "Invalid Argument DataPtw is Invalid");
    }
}


package com.sdone.createdailyptw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PermissionDeniedException extends ResponseStatusException {

    public PermissionDeniedException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}
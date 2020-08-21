package com.xtra.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFound extends RuntimeException {
    public EntityNotFound() {
    }

    public EntityNotFound(String message) {
        super(message);
    }

    public EntityNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFound(Throwable cause) {
        super(cause);
    }
}

package com.xtra.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActionNotAllowedException extends RuntimeException {
    private String errorCode;

    public ActionNotAllowedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}

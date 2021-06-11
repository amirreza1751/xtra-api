package com.xtra.api.model.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActionNotAllowedException extends RuntimeException {
    private ErrorCode errorCode;

    public ActionNotAllowedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}

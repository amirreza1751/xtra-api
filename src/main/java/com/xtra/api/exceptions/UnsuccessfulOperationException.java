package com.xtra.api.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnsuccessfulOperationException extends RuntimeException {
    private String errorCode;

    public UnsuccessfulOperationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}

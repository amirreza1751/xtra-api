package com.xtra.api.model.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IncorrectTotpException extends RuntimeException {
    private String details;
    private int errorCode = 511;
    public IncorrectTotpException() {
        details = "Totp is Incorrect.";
    }

}

package com.xtra.api.model.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TotpMissingException extends RuntimeException {
    private String details;
    private int errorCode = 510;
    public TotpMissingException() {
        details = "User needs to enter the totp.";
    }

}

package com.xtra.api.projection.reseller.subreseller;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreditChangeRequest {

    @NotNull(message = "change amount can not be empty")
    @Digits(message = "change amount should be a 5 digits number and 3 digit fraction at max", integer = 5, fraction = 3)
    private int changeAmount;

    @NotBlank(message = "description can not be empty")
    private String description;
}

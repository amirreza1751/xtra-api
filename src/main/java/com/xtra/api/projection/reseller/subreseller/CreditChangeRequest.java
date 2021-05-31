package com.xtra.api.projection.reseller.subreseller;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class CreditChangeRequest {

    @NotNull(message = "change amount can not be empty")
    @PositiveOrZero(message = "credits should be a positive number")
    private int changeAmount;

    @NotBlank(message = "description can not be empty")
    private String description;
}

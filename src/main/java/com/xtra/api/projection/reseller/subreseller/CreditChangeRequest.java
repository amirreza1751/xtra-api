package com.xtra.api.projection.reseller.subreseller;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreditChangeRequest {

    @NotNull(message = "change amount can not be empty")
    @Positive(message = "credits should be a positive number")
    private int credits;

    @NotBlank(message = "description can not be empty")
    private String description;
}

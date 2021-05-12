package com.xtra.api.projection.reseller.subreseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SubresellerCreateView {

    @NotBlank(message = "subreseller username can not be empty")
    private String username;
    @NotBlank(message = "subreseller password can not be empty")
    private String password;
    @Email
    private String email;

    @Digits(message = "change amount should be a 5 digits number and 3 digit fraction at max", integer = 5, fraction = 3)
    private int credits;
    @NotBlank(message = "subreseller DNS can not be empty")
    private String resellerDns;
}

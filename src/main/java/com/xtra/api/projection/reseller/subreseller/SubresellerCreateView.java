package com.xtra.api.projection.reseller.subreseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SubresellerCreateView {

    @NotBlank(message = "subreseller username can not be empty")
    private String username;
    @NotBlank(message = "subreseller password can not be empty")
    private String password;
    @Email
    private String email;

    @PositiveOrZero(message = "credits should be a positive number")
    private int credits;
    private String resellerDns;
}

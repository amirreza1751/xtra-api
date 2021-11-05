package com.xtra.api.projection.reseller.subreseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SubresellerCreateView {

    @NotBlank(message = "subreseller username can not be empty")
    private String username;
    @NotBlank(message = "subreseller password can not be empty")
    private String password;
    private String email;

    @PositiveOrZero(message = "credits should be a positive number")
    private int credits;
    @NotBlank(message = "subreseller DNS can not be empty")
    private String resellerDns;
}

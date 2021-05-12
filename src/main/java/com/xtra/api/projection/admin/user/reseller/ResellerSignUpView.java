package com.xtra.api.projection.admin.user.reseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import com.neovisionaries.i18n.CountryCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResellerSignUpView {
    @NotBlank(message = "reseller  username can not be empty")
    protected String username;
    @NotBlank(message = "reseller password is mandatory")
    protected String password;
    @Email
    protected String email;
    private List<CountryCode> mainMarket;
    private String telegramId;

}

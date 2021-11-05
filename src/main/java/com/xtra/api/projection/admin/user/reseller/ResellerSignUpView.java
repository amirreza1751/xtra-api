package com.xtra.api.projection.admin.user.reseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neovisionaries.i18n.CountryCode;
import lombok.Data;

import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResellerSignUpView {
    protected String username;
    protected String password;
    protected String email;
    private List<CountryCode> mainMarket;
    private String telegramId;

}

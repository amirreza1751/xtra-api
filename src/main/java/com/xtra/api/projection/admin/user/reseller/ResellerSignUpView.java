package com.xtra.api.projection.admin.user.reseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import com.neovisionaries.i18n.CountryCode;
import java.util.ArrayList;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResellerSignUpView {
    protected String username;
    protected String password;
    protected String email;
    private ArrayList<CountryCode> mainMarket;
    private String telegramId;

}

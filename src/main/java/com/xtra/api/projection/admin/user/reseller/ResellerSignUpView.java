package com.xtra.api.projection.admin.user.reseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResellerSignUpView {
    protected String username;
    protected String password;
    protected String email;
    private String mainMarket;
    private String telegramId;

}

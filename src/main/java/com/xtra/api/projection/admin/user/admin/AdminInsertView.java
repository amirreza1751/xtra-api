package com.xtra.api.projection.admin.user.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.projection.admin.user.UserInsertView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
public class AdminInsertView extends UserInsertView {

    @NotBlank(message = "admin first name can not be empty")
    private String firstname;
    @NotBlank(message = "admin last name can not be empty")
    private String lastname;
}

package com.xtra.api.projection.admin.user.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.projection.admin.user.UserInsertView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Validated
public class AdminInsertView extends UserInsertView {
    private String firstname;
    private String lastname;
}

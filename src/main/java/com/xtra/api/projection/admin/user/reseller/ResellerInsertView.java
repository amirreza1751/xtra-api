package com.xtra.api.projection.admin.user.reseller;

import com.xtra.api.projection.admin.user.UserInsertView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResellerInsertView extends UserInsertView {
    private boolean isVerified;
    @PositiveOrZero(message = "credits should be a positive number")
    private int credits;
    @NotBlank(message = "reseller DNS can not be empty")
    private String resellerDns;
    private String notes;
    private String lang;

    @NotNull(message = "reseller owner can not be empty")
    private Long ownerId;
}

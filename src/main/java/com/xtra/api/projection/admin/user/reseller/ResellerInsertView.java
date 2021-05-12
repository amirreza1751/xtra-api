package com.xtra.api.projection.admin.user.reseller;

import com.xtra.api.projection.admin.user.UserInsertView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResellerInsertView extends UserInsertView {
    private boolean isVerified;
    @Digits(message = "reseller credite should be a 5 digits number and 3 digit fraction max", integer = 5, fraction = 3)
    private int credits;
    @NotBlank(message = "reseller DNS can not be empty")
    private String resellerDns;
    private String notes;
    private String lang;

    @NotNull(message = "reseller owner can not be empty")
    private Long ownerId;
}

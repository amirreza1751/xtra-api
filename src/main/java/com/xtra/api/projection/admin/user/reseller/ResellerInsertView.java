package com.xtra.api.projection.admin.user.reseller;

import com.xtra.api.projection.admin.user.UserInsertView;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResellerInsertView extends UserInsertView {
    private boolean isVerified;
    private int credits;
    private String resellerDns;
    private String notes;
    private String lang;

    private Long ownerId;
}
